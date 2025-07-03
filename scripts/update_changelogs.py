#!/usr/bin/env python3

import subprocess
import re
import os
from datetime import date
from pathlib import Path
import sys

CHANGELOGS_DIR = Path("changelogs")

def get_last_bookmark_commits():
    try:
        result = subprocess.run(
            ["git", "log", "--grep=:bookmark:", "--pretty=format:%H%n%B%n---COMMIT_END---"],
            capture_output=True, text=True, check=True
        )

        commits = []
        commit_blocks = result.stdout.strip().split("---COMMIT_END---")

        for block in commit_blocks:
            if not block.strip():
                continue
            lines = block.strip().split("\n", 1)
            if len(lines) >= 2:
                commit_hash = lines[0]
                message = lines[1]
                commits.append((commit_hash, message))
            elif len(lines) == 1:
                # Handle case where there might be no body
                commit_hash = lines[0]
                commits.append((commit_hash, ""))

        return commits
    except subprocess.CalledProcessError as e:
        print(f"Failed to get bookmark commits: {e}")
        sys.exit(1)

def parse_current_release_modules(commits):
    for commit_hash, message in commits:
        if re.search(r":bookmark:.*Modules:", message):
            match = re.search(r"Modules:\s*(.+)", message)
            if match:
                module_str = match.group(1)
                modules = [m.strip() for m in module_str.split(",")]
                return commit_hash, modules
    print("No module release commit found.")
    sys.exit(0)

def find_last_release_commit(module, bookmark_commits, current_commit):
    """Find the last release commit for a specific module, looking backwards from current commit"""
    found_current = False
    for commit_hash, message in bookmark_commits:
        if commit_hash == current_commit:
            found_current = True
            continue
        if not found_current:
            continue

        # Look for the previous release of this specific module
        if module == "project":
            if ":bookmark:" in message and "[project]" in message:
                return commit_hash
        else:
            # Look for previous bookmark commits that mention this module
            if ":bookmark:" in message and (f"Bump {module} to" in message or module in message):
                return commit_hash
    return None

def get_commits_between(module, from_commit, to_commit):
    """Get commits between two points, filtering for the specific module"""
    if from_commit:
        # Get commits between the last release and current release
        range_str = f"{from_commit}..{to_commit}"
    else:
        # If no previous release found, get all commits up to current release
        range_str = to_commit

    try:
        result = subprocess.run(
            ["git", "log", range_str, "--pretty=format:%s"],
            capture_output=True, text=True, check=True
        )

        if not result.stdout.strip():
            return []

        commits = [line for line in result.stdout.strip().split("\n")
                   if line and line.startswith(f"[{module}]")]
        return commits
    except subprocess.CalledProcessError as e:
        print(f"Failed to get commits for module {module}: {e}")
        return []

def get_module_version(module, bookmark_commits, current_commit):
    if module == "project":
        return date.today().isoformat()

    # First, try to find version in the current release commit message
    for commit_hash, message in bookmark_commits:
        if commit_hash == current_commit:
            # Look for the new format: "Bump module `module` to version X.Y.Z"
            pattern1 = rf"Bump module `{module}` to version ([\d\.]+)"
            match = re.search(pattern1, message)
            if match:
                return match.group(1)

            # Look for alternative format: "Bump module {module} to version X.Y.Z"
            pattern2 = rf"Bump module {module} to version ([\d\.]+)"
            match = re.search(pattern2, message)
            if match:
                return match.group(1)

            # Look for old format: "Bump {module} to vX.Y.Z"
            pattern3 = rf"Bump {module} to v([\d\.]+)"
            match = re.search(pattern3, message)
            if match:
                return match.group(1)

            break

    # Fallback: look in previous commits (old behavior)
    for commit_hash, message in bookmark_commits:
        if commit_hash == current_commit:
            continue
        if f"Bump {module} to" in message:
            match = re.search(rf"Bump {module} to v([\d\.]+)", message)
            if match:
                return match.group(1)

    return None

def format_commit_message(commit_msg, module):
    """Remove module prefix from commit message if present"""
    prefix = f"[{module}] "
    if commit_msg.startswith(prefix):
        return commit_msg[len(prefix):]
    return commit_msg

def categorize_change(commit_msg):
    """Categorize a commit message based on gitmoji"""
    # Define category mappings
    categories = {
        "Features": [":sparkles:", ":boom:"],
        "Bug Fixes": [":bug:"],
        "Refactoring": [":recycle:"],
        "Documentation": [":memo:", ":books:"],
        "Performance": [":zap:"],
        "Style": [":art:", ":lipstick:"],
        "Tests": [":white_check_mark:", ":rotating_light:"],
        "Build": [":construction_worker:", ":green_heart:", ":arrow_up:", ":arrow_down:", ":pushpin:"],
        "CI/CD": [":construction_worker:", ":green_heart:"],
        "Security": [":lock:"],
        "Dependencies": [":arrow_up:", ":arrow_down:", ":pushpin:"],
        "Misc": []  # Fallback category
    }

    # Check each category for matching gitmojis
    for category, emojis in categories.items():
        if category == "Misc":
            continue
        for emoji in emojis:
            if emoji in commit_msg:
                return category

    # Default to Misc if no category found
    return "Misc"

def group_changes_by_category(changes, module):
    """Group changes by category while preserving order within each category"""
    categorized = {}
    category_order = []

    for change in changes:
        formatted_msg = format_commit_message(change, module)
        category = categorize_change(formatted_msg)

        if category not in categorized:
            categorized[category] = []
            category_order.append(category)

        categorized[category].append(formatted_msg)

    return categorized, category_order

def update_changelog(module, version, changes):
    if not changes:
        return

    filename = CHANGELOGS_DIR / f"{module}.md"
    filename.parent.mkdir(exist_ok=True)

    entry_title = version if module == "project" else f"{version} - {date.today().isoformat()}"

    # Group changes by category
    categorized_changes, category_order = group_changes_by_category(changes, module)

    # Build the changelog entry
    entry_parts = [f"## {entry_title}\n"]

    for category in category_order:
        if categorized_changes[category]:
            entry_parts.append(f"### {category}")
            for change in categorized_changes[category]:
                entry_parts.append(f"- {change}")
            entry_parts.append("")  # Add blank line after each category

    entry = "\n".join(entry_parts) + "\n"

    if filename.exists():
        with open(filename, "r") as f:
            old_content = f.read()
    else:
        old_content = f"# {module.capitalize()} Changelog\n\n"

    with open(filename, "w") as f:
        f.write(old_content + entry + "\n\n")

def main():
    bookmark_commits = get_last_bookmark_commits()
    current_release_commit, modules = parse_current_release_modules(bookmark_commits)

    for module in modules:
        last_commit = find_last_release_commit(module, bookmark_commits, current_release_commit)
        changes = get_commits_between(module, last_commit, current_release_commit)
        if not changes:
            print(f"No changes found for {module}, skipping.")
            continue
        version = get_module_version(module, bookmark_commits, current_release_commit)
        if not version:
            print(f"No version found for {module}, skipping.")
            continue
        print(f"Updating changelog for {module} with version {version}")
        update_changelog(module, version, changes)

if __name__ == "__main__":
    main()