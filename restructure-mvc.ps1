# PowerShell script to restructure the project to MVC architecture
$basePath = "c:\Users\moron\OneDrive\Documentos\Fall 25\CSE310\Module1\src\main\java\com\expensetracker\app"

Write-Host "Starting MVC restructuring..." -ForegroundColor Green

# Function to update package declaration and imports in a file
function Update-PackageAndImports {
    param(
        [string]$FilePath,
        [string]$NewPackage
    )
    
    if (Test-Path $FilePath) {
        $content = Get-Content $FilePath -Raw
        
        # Update package declaration
        $content = $content -replace 'package com\.expensetracker\.app\.domain\.entities;', "package $NewPackage;"
        $content = $content -replace 'package com\.expensetracker\.app\.domain\.enums;', "package $NewPackage;"
        $content = $content -replace 'package com\.expensetracker\.app\.domain\.repositories;', "package $NewPackage;"
        $content = $content -replace 'package com\.expensetracker\.app\.domain\.exceptions;', "package $NewPackage;"
        $content = $content -replace 'package com\.expensetracker\.app\.interfaces;', "package $NewPackage;"
        $content = $content -replace 'package com\.expensetracker\.app\.interfaces\.dto;', "package $NewPackage;"
        $content = $content -replace 'package com\.expensetracker\.app\.common;', "package $NewPackage;"
        $content = $content -replace 'package com\.expensetracker\.app\.security;', "package $NewPackage;"
        
        # Update imports
        $content = $content -replace 'import com\.expensetracker\.app\.domain\.entities\.', 'import com.expensetracker.app.models.'
        $content = $content -replace 'import com\.expensetracker\.app\.domain\.enums\.', 'import com.expensetracker.app.models.enums.'
        $content = $content -replace 'import com\.expensetracker\.app\.domain\.repositories\.', 'import com.expensetracker.app.repositories.'
        $content = $content -replace 'import com\.expensetracker\.app\.domain\.exceptions\.', 'import com.expensetracker.app.exceptions.'
        $content = $content -replace 'import com\.expensetracker\.app\.interfaces\.', 'import com.expensetracker.app.controllers.'
        $content = $content -replace 'import com\.expensetracker\.app\.interfaces\.dto\.', 'import com.expensetracker.app.dto.'
        $content = $content -replace 'import com\.expensetracker\.app\.common\.', 'import com.expensetracker.app.dto.'
        $content = $content -replace 'import com\.expensetracker\.app\.security\.', 'import com.expensetracker.app.services.'
        
        Set-Content -Path $FilePath -Value $content -NoNewline
        Write-Host "Updated: $FilePath" -ForegroundColor Yellow
    }
}

# Step 1: Copy and update entity files to models/
Write-Host "`n1. Moving entities to models/..." -ForegroundColor Cyan
$entityFiles = @("User.java", "Goal.java", "Expense.java")
foreach ($file in $entityFiles) {
    $source = Join-Path $basePath "domain\entities\$file"
    $dest = Join-Path $basePath "models\$file"
    if (Test-Path $source) {
        Copy-Item $source $dest -Force
        Update-PackageAndImports -FilePath $dest -NewPackage "com.expensetracker.app.models"
    }
}

# Step 2: Copy and update enum files to models/enums/
Write-Host "`n2. Moving enums to models/enums/..." -ForegroundColor Cyan
$enumFiles = @("UserRole.java", "GoalMode.java", "GoalStatus.java", "ExpenseCategory.java")
foreach ($file in $enumFiles) {
    $source = Join-Path $basePath "domain\enums\$file"
    $dest = Join-Path $basePath "models\enums\$file"
    if (Test-Path $source) {
        Copy-Item $source $dest -Force
        Update-PackageAndImports -FilePath $dest -NewPackage "com.expensetracker.app.models.enums"
    }
}

# Step 3: Copy and update repository files to repositories/
Write-Host "`n3. Moving repositories..." -ForegroundColor Cyan
$repoFiles = @("UserRepository.java", "GoalRepository.java", "ExpenseRepository.java")
foreach ($file in $repoFiles) {
    $source = Join-Path $basePath "domain\repositories\$file"
    $dest = Join-Path $basePath "repositories\$file"
    if (Test-Path $source) {
        Copy-Item $source $dest -Force
        Update-PackageAndImports -FilePath $dest -NewPackage "com.expensetracker.app.repositories"
    }
}

# Step 4: Copy and update controller files to controllers/
Write-Host "`n4. Moving controllers..." -ForegroundColor Cyan
$controllerFiles = @("UserController.java", "GoalController.java", "ExpenseController.java")
foreach ($file in $controllerFiles) {
    $source = Join-Path $basePath "interfaces\$file"
    $dest = Join-Path $basePath "controllers\$file"
    if (Test-Path $source) {
        Copy-Item $source $dest -Force
        Update-PackageAndImports -FilePath $dest -NewPackage "com.expensetracker.app.controllers"
    }
}

# Step 5: Copy and update DTO files to dto/
Write-Host "`n5. Moving DTOs..." -ForegroundColor Cyan
$dtoFiles = @("GoalRequest.java", "ExpenseRequest.java")
foreach ($file in $dtoFiles) {
    $source = Join-Path $basePath "interfaces\dto\$file"
    $dest = Join-Path $basePath "dto\$file"
    if (Test-Path $source) {
        Copy-Item $source $dest -Force
        Update-PackageAndImports -FilePath $dest -NewPackage "com.expensetracker.app.dto"
    }
}

# Move common/ files to dto/
$commonFiles = @("ApiResponse.java", "ApiError.java", "SuccessResponseAdvice.java", "GlobalExceptionHandler.java")
foreach ($file in $commonFiles) {
    $source = Join-Path $basePath "common\$file"
    $dest = Join-Path $basePath "dto\$file"
    if (Test-Path $source) {
        Copy-Item $source $dest -Force
        Update-PackageAndImports -FilePath $dest -NewPackage "com.expensetracker.app.dto"
    }
}

# Step 6: Copy and update exception files to exceptions/
Write-Host "`n6. Moving exceptions..." -ForegroundColor Cyan
$source = Join-Path $basePath "domain\exceptions\DuplicateEmailException.java"
$dest = Join-Path $basePath "exceptions\DuplicateEmailException.java"
if (Test-Path $source) {
    Copy-Item $source $dest -Force
    Update-PackageAndImports -FilePath $dest -NewPackage "com.expensetracker.app.exceptions"
}

# Step 7: Move SecurityService to services/
Write-Host "`n7. Moving SecurityService to services/..." -ForegroundColor Cyan
$source = Join-Path $basePath "security\SecurityService.java"
$dest = Join-Path $basePath "services\SecurityService.java"
if (Test-Path $source) {
    Copy-Item $source $dest -Force
    Update-PackageAndImports -FilePath $dest -NewPackage "com.expensetracker.app.services"
}

# Move SecurityFilter to services/
$source = Join-Path $basePath "security\SecurityFilter.java"
$dest = Join-Path $basePath "services\SecurityFilter.java"
if (Test-Path $source) {
    Copy-Item $source $dest -Force
    Update-PackageAndImports -FilePath $dest -NewPackage "com.expensetracker.app.services"
}

# Step 8: Update all remaining files with new imports
Write-Host "`n8. Updating imports in all files..." -ForegroundColor Cyan
Get-ChildItem -Path $basePath -Recurse -Filter "*.java" | ForEach-Object {
    Update-PackageAndImports -FilePath $_.FullName -NewPackage $null
}

# Step 9: Update test files
Write-Host "`n9. Updating test files..." -ForegroundColor Cyan
$testPath = "c:\Users\moron\OneDrive\Documentos\Fall 25\CSE310\Module1\src\test\java\com\expensetracker\app"
if (Test-Path $testPath) {
    Get-ChildItem -Path $testPath -Recurse -Filter "*.java" | ForEach-Object {
        Update-PackageAndImports -FilePath $_.FullName -NewPackage $null
    }
}

Write-Host "`nMVC restructuring complete!" -ForegroundColor Green
Write-Host "Please review the changes and run 'mvnw clean compile' to verify." -ForegroundColor Yellow
