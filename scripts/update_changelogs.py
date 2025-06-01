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
            ["git", "log", "--grep=:bookmark:", "--pretty=format:%H %s"],
            capture_output=True, text=True, check=True
        )
        lines = result.stdout.strip().split("\n")
        return [(line.split(" ", 1)[0], line.split(" ", 1)[1]) for line in lines]
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
    found_current = False
    for commit_hash, message in bookmark_commits:
        if commit_hash == current_commit:
            found_current = True
            continue
        if not found_current:
            continue
        if module == "project":
            if "[project]" in message:
                return commit_hash
        elif f"Bump {module} to" in message:
            return commit_hash
    return None

def get_commits_between(module, from_commit, to_commit):
    range_str = f"{from_commit}..{to_commit}" if from_commit else to_commit
    try:
        result = subprocess.run(
            ["git", "log", range_str, "--pretty=format:%s"],
            capture_output=True, text=True, check=True
        )
        commits = [line for line in result.stdout.strip().split("\n")
                   if line.startswith(f"[{module}]")]
        return commits
    except subprocess.CalledProcessError as e:
        print(f"Failed to get commits for module {module}: {e}")
        return []

def get_module_version(module, bookmark_commits, current_commit):
    if module == "project":
        return date.today().isoformat()
    for commit_hash, message in bookmark_commits:
        if commit_hash == current_commit:
            continue
        if f"Bump {module} to" in message:
            match = re.search(rf"Bump {module} to v([\d\.]+)", message)
            if match:
                return match.group(1)
    return None

def update_changelog(module, version, changes):
    if not changes:
        return

    filename = CHANGELOGS_DIR / f"{module}.md"
    filename.parent.mkdir(exist_ok=True)

    entry_title = version if module == "project" else f"{version} - {date.today().isoformat()}"
    entry = f"## {entry_title}\n" + "\n".join(f"- {c[1:-1]}" if c.startswith("[") else f"- {c}" for c in changes) + "\n\n"

    if filename.exists():
        with open(filename, "r") as f:
            old_content = f.read()
    else:
        old_content = f"# {module.capitalize()} Changelog\n\n"

    with open(filename, "w") as f:
        f.write(old_content.strip() + "\n\n" + entry)

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
