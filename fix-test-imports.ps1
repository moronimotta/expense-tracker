# Fix remaining test imports
$testPath = "c:\Users\moron\OneDrive\Documentos\Fall 25\CSE310\Module1\src\test\java"

Get-ChildItem -Path $testPath -Recurse -Filter "*.java" | ForEach-Object {
    $content = Get-Content $_.FullName -Raw
    
    # Fix any remaining .controllers.dto imports
    $content = $content -replace 'import com\.expensetracker\.app\.controllers\.dto\.', 'import com.expensetracker.app.dto.'
    
    Set-Content -Path $_.FullName -Value $content -NoNewline
    Write-Host "Fixed: $($_.FullName)"
}

Write-Host "All test imports fixed!"
