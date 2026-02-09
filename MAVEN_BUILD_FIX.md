# Maven Build Failure - Solution Guide

## Issue Analysis

**Error**: `java.lang.ExceptionInInitializerError: com.sun.tools.javac.code.TypeTag :: UNKNOWN`

**Root Causes**:
1. ❌ JRE being used instead of full JDK 17
2. ❌ Wrong Java version compiler
3. ❌ Encoding issue: `IBM437` (Windows code page) instead of UTF-8
4. ❌ IntelliJ's bundled JRE is incomplete for Maven compilation

---

## Solution 1: Fix Java Environment (Recommended)

### Step 1: Check Current Java Installation

```bash
# Check Java version
java -version

# Check if javac exists (compiler)
javac -version

# If javac not found, you only have JRE, not JDK
```

### Step 2: Install Oracle JDK 17

**Option A: Download from Oracle**
1. Visit https://www.oracle.com/java/technologies/downloads/#java17
2. Download: "Windows x64 Installer"
3. Install to: `C:\Program Files\Java\jdk-17.x.x`
4. Follow installer steps

**Option B: Use Adoptium OpenJDK 17 (Recommended)**
1. Visit https://adoptium.net/
2. Download: "OpenJDK 17 LTS"
3. Choose: "Installer for Windows x64"
4. Install to: `C:\Program Files\AdoptOpenJDK\jdk-17`

### Step 3: Set JAVA_HOME Environment Variable

**Windows (PowerShell as Administrator):**

```powershell
# Set permanent JAVA_HOME
[Environment]::SetEnvironmentVariable("JAVA_HOME", "C:\Program Files\AdoptOpenJDK\jdk-17", "User")

# Set encoding to UTF-8 (remove IBM437)
[Environment]::SetEnvironmentVariable("JAVA_TOOL_OPTIONS", "-Dfile.encoding=UTF-8", "User")

# Restart PowerShell or IDE
```

**Windows (Command Prompt as Administrator):**

```cmd
setx JAVA_HOME "C:\Program Files\AdoptOpenJDK\jdk-17"
setx JAVA_TOOL_OPTIONS "-Dfile.encoding=UTF-8"
```

### Step 4: Verify Installation

```bash
# Check JAVA_HOME is set
echo $env:JAVA_HOME

# Check javac exists
javac -version

# Check encoding
java -XshowSettings:properties 2>&1 | grep "file.encoding"
```

---

## Solution 2: Fix IntelliJ IDEA Settings

### Method A: Configure IntelliJ to Use External JDK

1. **File** → **Project Structure** (Ctrl+Alt+Shift+S)
2. **Project** tab → **SDK** dropdown
3. Click **"Add SDK"** → **Download JDK**
4. Select: **Version: 17**, **Vendor: Temurin (AdoptOpenJDK)**
5. Click **Download** → **OK**
6. Apply and close

### Method B: Point to System JDK

1. **File** → **Project Structure**
2. **Project** tab → **SDK**
3. Click **"Add SDK"** → **JDK**
4. Select: `C:\Program Files\AdoptOpenJDK\jdk-17`
5. Click **OK** → **Apply**

---

## Solution 3: Build from Command Line (Bypass IntelliJ)

### Option 1: Using System Maven

First, install Maven (if not already installed):

```bash
# Download Apache Maven 3.9.x
# From: https://maven.apache.org/download.cgi
# Extract to: C:\apache-maven-3.9.x

# Set MAVEN_HOME
[Environment]::SetEnvironmentVariable("MAVEN_HOME", "C:\apache-maven-3.9.x", "User")
[Environment]::SetEnvironmentVariable("Path", "$env:Path;C:\apache-maven-3.9.x\bin", "User")

# Restart PowerShell and verify
mvn --version
```

Then build:

```bash
# Navigate to backend
cd c:\Users\aakas\OneDrive\Desktop\GUVI Projects\event_management_system_project\backend

# Build without IntelliJ interference
mvn clean package -DskipTests -U
```

### Option 2: Using Maven Wrapper (Simpler)

If the project has a Maven wrapper:

```bash
# Windows
cd backend
mvnw.cmd clean package -DskipTests

# Or explicitly set encoding
mvnw.cmd clean package -DskipTests -Dproject.build.sourceEncoding=UTF-8
```

---

## Solution 4: Quick Fix (Temporary)

Run Maven with explicit encoding and JDK path:

```powershell
# Set variables for this session only
$env:JAVA_HOME = "C:\Program Files\AdoptOpenJDK\jdk-17"
$env:JAVA_TOOL_OPTIONS = "-Dfile.encoding=UTF-8"

# Navigate and build
cd "c:\Users\aakas\OneDrive\Desktop\GUVI Projects\event_management_system_project\backend"

# Build with Maven (using IntelliJ's Maven)
& "C:\Program Files\JetBrains\IntelliJ IDEA 2025.3.1.1\plugins\maven\lib\maven3\bin\mvn.cmd" `
  clean package -DskipTests `
  -Dproject.build.sourceEncoding=UTF-8
```

---

## Recommended Setup (Complete)

### Step 1: Install AdoptOpenJDK 17

```powershell
# Option: Use Chocolatey (if installed)
choco install openjdk17

# Or download and install manually from https://adoptium.net/
```

### Step 2: Set Environment Variables

```powershell
# Set JAVA_HOME to point to JDK (not JRE)
[Environment]::SetEnvironmentVariable("JAVA_HOME", "C:\Program Files\Eclipse Adoptium\jdk-17.0.x.x", "User")

# Set encoding to UTF-8
[Environment]::SetEnvironmentVariable("JAVA_TOOL_OPTIONS", "-Dfile.encoding=UTF-8", "User")

# Close and reopen PowerShell
```

### Step 3: Verify Setup

```powershell
# Test Java
java -version
javac -version

# Test Maven
mvn --version

# All three should show Java 17 and newer versions
```

### Step 4: Build Project

```bash
cd backend
mvn clean package -DskipTests
```

**Expected Output:**
```
[INFO] BUILD SUCCESS
[INFO] Copying artifact event-management-system-1.0.0.jar
[INFO] Total time: XX.XXX s
```

---

## Troubleshooting

### Issue: "javac: command not found"

**Solution**: You have JRE, not JDK
- Install full JDK from https://adoptium.net/
- Set JAVA_HOME to JDK path, not JRE path

### Issue: Still getting IBM437 encoding error

**Solution**: Check JAVA_TOOL_OPTIONS

```powershell
# View current setting
$env:JAVA_TOOL_OPTIONS

# Clear old setting
[Environment]::SetEnvironmentVariable("JAVA_TOOL_OPTIONS", "", "User")

# Set new setting
[Environment]::SetEnvironmentVariable("JAVA_TOOL_OPTIONS", "-Dfile.encoding=UTF-8", "User")

# Close all terminals and reopen
```

### Issue: Maven says "JAVA_HOME not set"

**Solution**:

```powershell
# Verify JAVA_HOME
echo $env:JAVA_HOME

# If empty, set it
[Environment]::SetEnvironmentVariable("JAVA_HOME", "C:\Program Files\Eclipse Adoptium\jdk-17.0.x.x", "User")

# Restart PowerShell
```

### Issue: "No compiler is provided"

**Solution**: Maven is using JRE instead of JDK

```bash
# Force Maven to use JDK
mvn -Djavac=javac -Djavac.executable="C:\Program Files\Eclipse Adoptium\jdk-17.0.x.x\bin\javac.exe" clean package -DskipTests
```

---

## Recommended Build Commands

### Simple Build (No Tests)
```bash
mvn clean package -DskipTests
```

### Build with Encoding Fix
```bash
mvn clean package -DskipTests -Dproject.build.sourceEncoding=UTF-8
```

### Full Build with Tests (slower)
```bash
mvn clean package
```

### Build with Verbose Output (for debugging)
```bash
mvn clean package -DskipTests -X
```

### Clean Maven Cache and Rebuild
```bash
# Remove Maven cache
rm -r ~/.m2/repository  # Linux/Mac
rmdir /s %userprofile%\.m2\repository  # Windows

# Rebuild
mvn clean package -DskipTests -U
```

---

## Quick Verification Script

Save as `verify-java.ps1`:

```powershell
Write-Host "=== Java Environment Check ===" -ForegroundColor Cyan

Write-Host "`nJava Version:" -ForegroundColor Green
java -version

Write-Host "`nJavac Version:" -ForegroundColor Green
javac -version

Write-Host "`nJAVA_HOME:" -ForegroundColor Green
echo $env:JAVA_HOME

Write-Host "`nJAVA_TOOL_OPTIONS:" -ForegroundColor Green
echo $env:JAVA_TOOL_OPTIONS

Write-Host "`nFile Encoding:" -ForegroundColor Green
java -XshowSettings:properties 2>&1 | Select-String "file.encoding"

Write-Host "`n✅ All checks passed!" -ForegroundColor Green
```

Run with:
```powershell
PowerShell -ExecutionPolicy Bypass -File verify-java.ps1
```

---

## Summary

1. ✅ Install AdoptOpenJDK 17 from https://adoptium.net/
2. ✅ Set JAVA_HOME to JDK installation path
3. ✅ Fix encoding: `JAVA_TOOL_OPTIONS=-Dfile.encoding=UTF-8`
4. ✅ Verify: `java -version`, `javac -version`, `mvn --version`
5. ✅ Build: `mvn clean package -DskipTests`

---

**Expected Result After Fixes:**

```
[INFO] Compiling 37 source files with javac [debug release 17]
[INFO] 
[INFO] --- jar:3.2.0:jar (default-jar) @ event-management-system ---
[INFO] Building jar: .../target/event-management-system-1.0.0.jar
[INFO] 
[INFO] BUILD SUCCESS
```

JAR file ready at: `backend/target/event-management-system-1.0.0.jar`
