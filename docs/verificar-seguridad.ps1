# =====================================================================
# Verificacion de la Fase 3 (Spring Security + BCrypt + JWT)
# =====================================================================
# Ejecuta comprobaciones reales contra la aplicacion y devuelve
# PASA / FALLA por cada punto. Requiere la app corriendo en el puerto 8080.
#
#   powershell -ExecutionPolicy Bypass -File docs\verificar-seguridad.ps1
#
# Parametros:
#   -BaseUrl    URL de la app        (por defecto http://localhost:8080)
#   -SinTests   no ejecutar mvn test  (por defecto si se ejecuta)
# =====================================================================

param(
    [string]$BaseUrl = "http://localhost:8080",
    [switch]$SinTests
)

$ErrorActionPreference = "Continue"
$global:ContadorOk = 0
$global:ContadorFail = 0

function Titulo($t) { Write-Host "`n=== $t ===" -ForegroundColor Cyan }
function Pasa($t) { $global:ContadorOk = $global:ContadorOk + 1; Write-Host "  [PASA]  $t" -ForegroundColor Green }
function Falla($t, $d) { $global:ContadorFail = $global:ContadorFail + 1; Write-Host "  [FALLA] $t" -ForegroundColor Red; if ($d) { Write-Host "          $d" -ForegroundColor DarkGray } }
function Info($t) { Write-Host "  ....    $t" -ForegroundColor DarkGray }

function Get-Status($url, $method = "GET", $headers = $null, $body = $null) {
    try {
        $p = @{ Uri = $url; Method = $method; Headers = $headers; UseBasicParsing = $true; TimeoutSec = 20; ErrorAction = "Stop" }
        if ($body) { $p.Body = $body; $p.ContentType = "application/json" }
        return (Invoke-WebRequest @p).StatusCode
    } catch {
        if ($_.Exception.Response) { return $_.Exception.Response.StatusCode.value__ }
        return 0
    }
}

function Get-Body($url, $method = "GET", $headers = $null, $body = $null) {
    try {
        $p = @{ Uri = $url; Method = $method; Headers = $headers; UseBasicParsing = $true; TimeoutSec = 20; ErrorAction = "Stop" }
        if ($body) { $p.Body = $body; $p.ContentType = "application/json" }
        return (Invoke-WebRequest @p).Content
    } catch { return "" }
}

function Decode-Jwt($token) {
    $part = $token.Split('.')[1].Replace('-', '+').Replace('_', '/')
    while ($part.Length % 4) { $part += '=' }
    return [System.Text.Encoding]::UTF8.GetString([Convert]::FromBase64String($part)) | ConvertFrom-Json
}

function Decode-Jwt-Parte($token, $indice) {
    $part = $token.Split('.')[$indice].Replace('-', '+').Replace('_', '/')
    while ($part.Length % 4) { $part += '=' }
    return [System.Text.Encoding]::UTF8.GetString([Convert]::FromBase64String($part))
}

# ---------------------------------------------------------------------
Titulo "0. La aplicacion esta arriba"
if ((Get-Status "$BaseUrl/api/info") -eq 200) { Pasa "API respondiendo en $BaseUrl" }
else { Falla "API no responde en $BaseUrl" -d "Levantala con: mvn spring-boot:run"; exit 1 }

# ---------------------------------------------------------------------
Titulo "1. Spring Security y BCrypt"

$deps = & ".\mvnw.cmd" dependency:tree "-Dincludes=org.springframework.security:*,io.jsonwebtoken:*" 2>&1 | Out-String
if ($deps -match "spring-boot-starter-security") { Pasa "Dependencia spring-boot-starter-security presente" }
else { Falla "Falta spring-boot-starter-security" }

$secVer = [regex]::Match($deps, "spring-security-core:jar:([\d\.]+)").Groups[1].Value
if ($secVer) { Info "Version de Spring Security: $secVer" }

if ($deps -match "jjwt-api:jar:([\d\.]+)") { Pasa "Dependencia JJWT presente (version $($matches[1]))" }
else { Falla "Falta io.jsonwebtoken:jjwt-api" }

# El PasswordEncoder debe ser BCrypt: se comprueba con el codigo fuente
$encoder = Get-Content "src\main\java\sv\edu\udb\cfcconnect\config\SecurityConfig.java" -Raw
if ($encoder -match "BCryptPasswordEncoder") { Pasa "PasswordEncoder configurado como BCryptPasswordEncoder" }
else { Falla "No se encontro BCryptPasswordEncoder en SecurityConfig" }

# La API nunca debe exponer el password
$admin = Invoke-RestMethod -Uri "$BaseUrl/auth/login" -Method Post -ContentType "application/json" `
    -Body '{"email":"admin@cfc.edu.sv","password":"Admin123*"}'
$hAdmin = @{ Authorization = "Bearer $($admin.token)" }

$usuarios = Get-Body "$BaseUrl/usuarios" "GET" $hAdmin
if ($usuarios -notmatch '"password"') { Pasa "La API no expone el campo password en /usuarios" }
else { Falla "La API esta exponiendo el password" }

# ---------------------------------------------------------------------
Titulo "2. Proteccion de la administracion de usuarios"

if ((Get-Status "$BaseUrl/usuarios") -eq 401) { Pasa "GET /usuarios sin token -> 401 Unauthorized" }
else { Falla "GET /usuarios sin token deberia dar 401" -d " dio: $(Get-Status "$BaseUrl/usuarios")" }

if ((Get-Status "$BaseUrl/usuarios" "GET" $hAdmin) -eq 200) { Pasa "GET /usuarios con token ADMIN -> 200 OK" }
else { Falla "GET /usuarios con token ADMIN deberia dar 200" }

$rec = Invoke-RestMethod -Uri "$BaseUrl/auth/login" -Method Post -ContentType "application/json" `
    -Body '{"email":"recepcion@cfc.edu.sv","password":"Recepcion123*"}'
$hRec = @{ Authorization = "Bearer $($rec.token)" }
if ((Get-Status "$BaseUrl/usuarios" "GET" $hRec) -eq 403) { Pasa "GET /usuarios con token RECEPCIONISTA -> 403 Forbidden" }
else { Falla "GET /usuarios con RECEPCIONISTA deberia dar 403" -d " dio: $(Get-Status "$BaseUrl/usuarios" 'GET' $hRec)" }

if ((Get-Status "$BaseUrl/roles" "GET" $hRec) -eq 403) { Pasa "GET /roles con token RECEPCIONISTA -> 403 Forbidden" }
else { Falla "GET /roles con RECEPCIONISTA deberia dar 403" }

# ---------------------------------------------------------------------
Titulo "3. Emision de tokens JWT"

if ($admin.token -and $admin.token.Split('.').Count -eq 3) { Pasa "El login devuelve un token con 3 segmentos (header.payload.firma)" }
else { Falla "El token no tiene el formato JWT esperado" }

$claims = Decode-Jwt $admin.token
if ($claims.sub -eq "admin@cfc.edu.sv") { Pasa "Claim 'sub' (correo) = $($claims.sub)" } else { Falla "Claim 'sub' incorrecto" }
if ($claims.rol -eq "ADMIN") { Pasa "Claim 'rol' = $($claims.rol)" } else { Falla "Claim 'rol' incorrecto" }
if ($claims.exp -and $claims.iat) { Pasa "Claims 'iat' y 'exp' presentes" } else { Falla "Faltan los claims de tiempo" }

$header = Decode-Jwt-Parte $admin.token 0
if ($header -match 'HS256') { Pasa "El token esta firmado con HS256" } else { Falla "Algoritmo de firma inesperado: $header" }

# ---------------------------------------------------------------------
Titulo "4. Validacion de tokens"

if ((Get-Status "$BaseUrl/clientes" "GET" $hAdmin) -eq 200) { Pasa "GET /clientes con token valido -> 200 OK" }
else { Falla "GET /clientes con token valido deberia dar 200" }

if ((Get-Status "$BaseUrl/clientes") -eq 401) { Pasa "GET /clientes sin token -> 401 Unauthorized" }
else { Falla "GET /clientes sin token deberia dar 401" }

$basura = @{ Authorization = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbkBjZmMuIn0.firma-falsa" }
if ((Get-Status "$BaseUrl/clientes" "GET" $basura) -eq 401) { Pasa "GET /clientes con token invalido -> 401 Unauthorized" }
else { Falla "Un token invalido deberia dar 401" }

$partes = $admin.token.Split('.')
$partes[1] = "X" + $partes[1].Substring(1)
$manipulado = @{ Authorization = "Bearer $($partes -join '.')" }
if ((Get-Status "$BaseUrl/clientes" "GET" $manipulado) -eq 401) { Pasa "GET /clientes con token manipulado -> 401 Unauthorized" }
else { Falla "Un token manipulado deberia dar 401" }

# ---------------------------------------------------------------------
Titulo "5. Expiracion configurable"

$exp = [int]$claims.exp - [int]$claims.iat
if ($admin.expiresIn -eq 3600) { Pasa "Duracion configurada: $($admin.expiresIn) segundos (app.jwt.expiration-seconds)" }
else { Falla "Duracion inesperada" -d " expiresIn=$($admin.expiresIn)" }

if ($exp -eq $admin.expiresIn) { Pasa "El claim 'exp' coincide con la duracion configurada ($exp s)" }
else { Falla "El claim 'exp' no coincide con expiresIn" -d " exp=$exp expiresIn=$($admin.expiresIn)" }

# Prueba real de expiracion: segunda instancia con expiracion de 1 segundo
$jar = Get-ChildItem "target\*.jar" | Where-Object { $_.Name -notlike "*.original" } | Select-Object -First 1
if ($jar -and $env:JAVA_HOME) {
    Info "Levantando una 2a instancia en el puerto 8091 con expiracion de 1 segundo..."
    $proc = Start-Process -FilePath "$env:JAVA_HOME\bin\java.exe" `
        -ArgumentList "-jar", $jar.FullName, "--server.port=8091", "--app.jwt.expiration-seconds=1" `
        -WindowStyle Hidden -PassThru
    try {
        $arrancada = $false
        for ($i = 0; $i -lt 40; $i++) {
            if ((Get-Status "http://localhost:8091/api/info") -eq 200) { $arrancada = $true; break }
            Start-Sleep -Milliseconds 500
        }
        if ($arrancada) {
            $corto = Invoke-RestMethod -Uri "http://localhost:8091/auth/login" -Method Post -ContentType "application/json" `
                -Body '{"email":"admin@cfc.edu.sv","password":"Admin123*"}'
            $hc = @{ Authorization = "Bearer $($corto.token)" }
            if ((Get-Status "http://localhost:8091/clientes" "GET" $hc) -eq 200) { Pasa "Token valido (expira en 1s) -> 200 OK" }
            else { Falla "El token recien emitido deberia ser valido" }
            Start-Sleep -Seconds 3
            if ((Get-Status "http://localhost:8091/clientes" "GET" $hc) -eq 401) { Pasa "Token expirado (3 s despues) -> 401 Unauthorized  <-- la expiracion se aplica de verdad" }
            else { Falla "Un token expirado deberia dar 401" -d " dio: $(Get-Status 'http://localhost:8091/clientes' 'GET' $hc)" }
        } else { Info "No se pudo levantar la 2a instancia; se omite la prueba de expiracion" }
    } finally {
        Stop-Process -Id $proc.Id -Force -ErrorAction SilentlyContinue
    }
} else { Info "Falta el jar o JAVA_HOME; se omite la prueba de expiracion en vivo" }

# ---------------------------------------------------------------------
if (-not $SinTests) {
    Titulo "6. Pruebas unitarias (incluye el hash BCrypt)"
    $out = & ".\mvnw.cmd" test 2>&1 | Out-String
    $todos = [regex]::Matches($out, "Tests run: (\d+), Failures: (\d+), Errors: (\d+)")
    if ($todos.Count -gt 0) {
        $ultimo = $todos[$todos.Count - 1]
        $total = $ultimo.Groups[1].Value; $f = $ultimo.Groups[2].Value; $e = $ultimo.Groups[3].Value
        if ($f -eq "0" -and $e -eq "0") { Pasa "mvn test -> $total pruebas, 0 fallos, 0 errores" }
        else { Falla "mvn test -> $total pruebas, $f fallos, $e errores" }
    } else { Falla "No se pudo ejecutar mvn test" }
}

# ---------------------------------------------------------------------
Titulo "RESUMEN"
Write-Host "  PASA:  $global:ContadorOk" -ForegroundColor Green
Write-Host "  FALLA: $global:ContadorFail" -ForegroundColor Red
if ($global:ContadorFail -eq 0) {
    Write-Host "`n  TODO CORRECTO: la Fase 3 esta implementada y funcionando.`n" -ForegroundColor Green
} else {
    Write-Host "`n  HAY FALLAS: revisa los puntos marcados arriba.`n" -ForegroundColor Red
}
Write-Host "  Prueba complementaria (hash BCrypt en la base de datos):"
Write-Host "    http://localhost:8080/h2-console  ->  JDBC URL: jdbc:h2:mem:cfcconnect  (user: sa, sin password)"
Write-Host "    SELECT email, password FROM usuarios;   ->  debe mostrar `$2a`10`...`n"
exit ([int]($global:ContadorFail -gt 0))
