#!/usr/bin/env python3

import subprocess
import re
import os
import sys

def get_last_commit_message():
    try:
        result = subprocess.run(["git", "log", "-1", "--pretty=%B"], capture_output=True, text=True, check=True)
        return result.stdout.strip()
    except subprocess.CalledProcessError as e:
        print(f"❌ Failed to get commit message: {e}")
        sys.exit(1)

def extract_modules(commit_msg):
    pattern = r":bookmark:\s*\[RELEASE\]\s*Modules:\s*([a-zA-Z0-9_,\s-]+)"
    match = re.search(pattern, commit_msg)
    if not match:
        print("⚠️ No release modules found in the commit message.")
        return []

    module_list = match.group(1)
    modules = [m.strip() for m in module_list.split(",") if m.strip()]
    return modules

def release_modules(modules):
    gradlew = os.path.join(os.path.dirname(__file__), "..", "gradlew")
    for module in modules:
        print(f"🚀 Releasing module: {module}")
        try:
            subprocess.run([gradlew, f":{module}:publishAndReleaseToMavenCentral"], check=True)
        except subprocess.CalledProcessError as e:
            print(f"❌ Release failed for module '{module}': {e}")
            sys.exit(1)

def main():
    commit_msg = get_last_commit_message()
    modules = extract_modules(commit_msg)

    if not modules:
        print("✅ No modules to release.")
        return

    release_modules(modules)
    print("🎉 Release process completed.")

if __name__ == "__main__":
    main()
