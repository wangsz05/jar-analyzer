# RELEASE DOC

Use `Eclipse Temurin JRE 8`

- https://adoptium.net/zh-CN/temurin/releases/?os=windows&arch=x64&package=jre&version=8

Use `Eclipse Temurin JRE 21`

- https://adoptium.net/zh-CN/temurin/releases/?os=windows&arch=x64&package=jre&version=21

Jar Analyzer Workflow JRE Repo

- https://github.com/jar-analyzer/workflow_jre

Steps:

- Run `go run .\github\main.go proxy` to clean actions
- Run `me/n1ar4/support/Contributor.java` to generate thanks.txt
- Run `me/n1ar4/support/ContributorMD.java` to generate thanks.md
- Run `check-version.bat` and update `pom.xml`
- Check `me/n1ar4/jar/analyzer/starter/Const.java` version
- Check `build/*.bat` files
- Check `pom.xml` version tag
- Check `build.py` VERSION
- Check `build.yml` VERSION
- Run git commit and push
- Github Action Build
- Change `version.txt` on `OSS Browser`
