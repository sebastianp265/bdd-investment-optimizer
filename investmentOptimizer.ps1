#!/usr/bin/env pwsh

param(
    [Parameter(ValueFromRemainingArguments=$true)]
    [string[]]$Args
)

$scriptPath = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location $scriptPath

if ($Args.Count -eq 0) {
    & .\gradlew run
} else {
    & .\gradlew run --args="$($Args -join ' ')"
}

