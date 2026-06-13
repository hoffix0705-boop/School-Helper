$BASE = "http://localhost:8080"

# 登录 admin 账号
$login = Invoke-RestMethod -Uri "$BASE/auth/login" -Method POST -ContentType "application/json" -Body '{"username":"admin","password":"admin123"}'
$headers = @{ "token" = $login.data.token }

# 清理旧分类
$tree = Invoke-RestMethod -Uri "$BASE/categories/tree" -Method GET -Headers $headers
foreach ($c in $tree.data) {
    # 先删除子分类
    foreach ($cc in $c.children) {
        Invoke-RestMethod -Uri "$BASE/categories/$($cc.id)" -Method DELETE -Headers $headers
        Write-Host "  删除子分类: $($cc.name) (ID=$($cc.id))" -ForegroundColor Gray
    }
    # 再删除一级分类
    Invoke-RestMethod -Uri "$BASE/categories/$($c.id)" -Method DELETE -Headers $headers
    Write-Host "  删除一级分类: $($c.name) (ID=$($c.id))" -ForegroundColor Gray
}
Write-Host "旧分类已清除" -ForegroundColor Yellow

# 创建一级分类
Write-Host ">>> 创建一级分类..." -ForegroundColor Green
function Create-Category($name, $parentId, $sort, $icon) {
    $body = @{
        name     = $name
        parentId = $parentId
        sort     = $sort
        icon     = $icon
        status   = 1
    } | ConvertTo-Json
    $r = Invoke-RestMethod -Uri "$BASE/categories" -Method POST -ContentType "application/json" -Headers $headers -Body $body
    Write-Host "  创建分类 '$name' 成功" -ForegroundColor Green
    return $r.data.id
}

Create-Category "学习辅导"  $null 1 "Book"
Create-Category "生活服务"  $null 2 "Coffee"
Create-Category "校园跑腿"  $null 3 "Bike"
Create-Category "二手交易"  $null 4 "Shopping"
Create-Category "社团活动"  $null 5 "Flag"

Write-Host "操作完成" -ForegroundColor Cyan
