# Fix test files to use new SecurityService method names

$testFiles = @(
    "src\test\java\com\expensetracker\app\ExpenseTrackerIntegrationTest.java",
    "src\test\java\com\expensetracker\app\SecurityTest.java",
    "src\test\java\com\expensetracker\app\security\SecurityServiceTest.java"
)

foreach ($file in $testFiles) {
    $fullPath = Join-Path $PSScriptRoot $file
    if (Test-Path $fullPath) {
        $content = Get-Content $fullPath -Raw
        
        # Replace setCurrentUserId with setCurrentUser
        $content = $content -replace 'setCurrentUserId', 'setCurrentUser'
        
        # Fix SecurityService constructor - add userRepository parameter
        $content = $content -replace 'securityService = new SecurityService\(\);', 'securityService = new SecurityService(userRepository);'
        
        Set-Content $fullPath $content -NoNewline
        Write-Host "Fixed: $file"
    }
}

Write-Host "All test method names updated!"
