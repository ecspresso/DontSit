# DontSit
Application to prevent prolonged sitting

Manual install
1. Place DontSit.jar in `C:\Users\REPLACE_WITH_YOUR_USERNAME\AppData\Roaming\Microsoft\Windows\Start Menu\Programs\Startup\`
2. It will now start when your computer starts.  

OR  

1. Place DontSit.jar somewhere (ex. `C:\Program Files\DontSit\DontSit.jar`)
2. Open regedit
3. Go to `HKEY_LOCAL_MACHINE\Software\Microsoft\Windows\CurrentVersion\Run`
4. Add new string value and give it a name (ex. `DontSit`)
5. Set the value to the full path of DontSit.jar (ex `"C:\Program Files\DontSit\DontSit.jar"`)
6. It will now start when your computer starts.

Windows script
Download and run https://github.com/ecspresso/DontSit/blob/master/install.ps1.

Windows installer to come.  
Mac and Linux scripts to come.
