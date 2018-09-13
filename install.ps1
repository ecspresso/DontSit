$file = "C:\Users\$env:USERNAME\AppData\Roaming\Microsoft\Windows\Start Menu\Programs\Startup\DontSit.jar"
$startup = "C:\Users\$env:USERNAME\AppData\Roaming\Microsoft\Windows\Start Menu\Programs\Startup\"

while(Test-Path $file) {
    rm $file
    if(!$?) {
        Write-Host "Program is still running. Close program and press any key to continue."
        $null = $Host.UI.RawUI.ReadKey('NoEcho,IncludeKeyDown');
    } else {
        break
    }
}

[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12
Invoke-WebRequest $((Invoke-RestMethod -Uri https://api.github.com/repos/ecspresso/DontSit/releases/latest).Assets.browser_download_url | Where {$_ -like "*DontSit.jar" }) -OutFile $($startup + "DontSit.jar")
Invoke-Item $startup