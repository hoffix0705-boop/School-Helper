# 校园帮种子数据脚本
# 使用方法: .\seed-data.ps1

$BASE = "http://localhost:8080"
$token = $null

function Headers { @{ "token" = $token } }

# ---------- 注册用户 ----------
function Register-User($username, $password, $nickname, $phone) {
    $body = @{
        username = $username
        password = $password
        nickname = $nickname
        phone    = $phone
    } | ConvertTo-Json
    
    try {
        $r = Invoke-RestMethod -Uri "$BASE/auth/register" -Method POST -ContentType "application/json" -Body $body
        Write-Host "  [$($r.code)] 注册 $username → $($r.msg)"
    } catch {
        Write-Host "  [跳过] $username : $($_.Exception.Message)" -ForegroundColor Yellow
    }
}

# ---------- 登录 ----------
function Login-User($username, $password) {
    $body = @{ username = $username; password = $password } | ConvertTo-Json
    $r = Invoke-RestMethod -Uri "$BASE/auth/login" -Method POST -ContentType "application/json" -Body $body
    if ($r.code -eq 200) {
        $global:token = $r.data.token
        return $r.data.token
    }
    return $null
}

# ---------- 创建分类 ----------
function Create-Category($name, $parentId, $sort, $icon) {
    $body = @{
        name     = $name
        parentId = $parentId
        sort     = $sort
        icon     = $icon
        status   = 1
    } | ConvertTo-Json
    $r = Invoke-RestMethod -Uri "$BASE/categories" -Method POST -ContentType "application/json" -Headers (Headers) -Body $body
    Write-Host "  [$($r.code)] 创建分类 '$name'"
}

# ---------- 创建任务 ----------
function Create-Task($title, $desc, $categoryId, $price, $urgent, $deadlineDays) {
    $deadline = (Get-Date).AddDays($deadlineDays).ToString("yyyy-MM-ddTHH:mm:ss")
    $body = @{
        title       = $title
        description = $desc
        categoryId  = $categoryId
        price       = $price
        urgent      = $urgent
        contactPhone = "13800138000"
        address     = "校园A区"
        deadline    = $deadline
    } | ConvertTo-Json -Depth 3
    $r = Invoke-RestMethod -Uri "$BASE/tasks" -Method POST -ContentType "application/json" -Headers (Headers) -Body $body
    Write-Host "  [$($r.code)] 创建任务 '$title'"
    return $r.data.id
}

# ---------- 接单 ----------
function Accept-Task($taskId) {
    $r = Invoke-RestMethod -Uri "$BASE/tasks/$taskId/accept" -Method POST -ContentType "application/json" -Headers (Headers)
    Write-Host "  [$($r.code)] 接单 taskId=$taskId"
}

# ---------- 完成任务 ----------
function Complete-Task($taskId) {
    $r = Invoke-RestMethod -Uri "$BASE/tasks/$taskId/accept/complete" -Method PUT -ContentType "application/json" -Headers (Headers)
    Write-Host "  [$($r.code)] 完成 taskId=$taskId"
}

# =================== 主流程 ===================
Write-Host "========== 校园帮种子数据开始 ==========" -ForegroundColor Cyan
Write-Host ""

# 1. 注册学生用户
Write-Host ">>> 注册学生用户..." -ForegroundColor Green
Register-User "zhangsan"  "pass123"  "张三"  "13800000001"
Register-User "lisi"      "pass123"  "李四"  "13800000002"
Register-User "wangwu"    "pass123"  "王五"  "13800000003"
Register-User "zhaoliu"   "pass123"  "赵六"  "13800000004"
Register-User "sunqi"     "pass123"  "孙七"  "13800000005"

# 2. 登录 admin 创建分类
Write-Host "`n>>> 创建分类..." -ForegroundColor Green
Login-User "admin" "admin123"

# 一级分类
Create-Category "学习辅导"  0 1 "Book"
Create-Category "生活服务"  0 2 "Coffee"
Create-Category "校园跑腿"  0 3 "Bike"
Create-Category "二手交易"  0 4 "Shopping"
Create-Category "社团活动"  0 5 "Flag"

# 二级分类（获取分类列表拿ID）
$cats = Invoke-RestMethod -Uri "$BASE/categories/tree" -Method GET -Headers (Headers)
function Get-CatId($name) { return ($cats.data | Where-Object { $_.name -eq $name }).id }

$studyId   = Get-CatId "学习辅导"
$lifeId    = Get-CatId "生活服务"
$errandId  = Get-CatId "校园跑腿"
$tradeId   = Get-CatId "二手交易"
$clubId    = Get-CatId "社团活动"

Create-Category "高数辅导"   $studyId  1 "Book"
Create-Category "英语辅导"   $studyId  2 "Book"
Create-Category "编程辅导"   $studyId  3 "Code"
Create-Category "代取快递"   $errandId 1 "Package"
Create-Category "食堂代购"   $errandId 2 "Coffee"
Create-Category "打印资料"   $lifeId   1 "File"
Create-Category "维修电脑"   $lifeId   2 "Monitor"
Create-Category "教材转让"   $tradeId  1 "Book"
Create-Category "电子产品"   $tradeId  2 "Smartphone"

# 重新获取分类树来获取子类ID
$cats = Invoke-RestMethod -Uri "$BASE/categories/tree" -Method GET -Headers (Headers)
function Get-SubCatId($parentName, $childName) {
    $parent = $cats.data | Where-Object { $_.name -eq $parentName }
    if ($parent) { $child = $parent.children | Where-Object { $_.name -eq $childName }; if ($child) { return $child.id } }
    return $null
}

$gaoshuId     = Get-SubCatId "学习辅导" "高数辅导"
$englishId    = Get-SubCatId "学习辅导" "英语辅导"
$codingId     = Get-SubCatId "学习辅导" "编程辅导"
$packageId    = Get-SubCatId "校园跑腿" "代取快递"
$canteenId    = Get-SubCatId "校园跑腿" "食堂代购"
$printId      = Get-SubCatId "生活服务" "打印资料"
$repairId     = Get-SubCatId "生活服务" "维修电脑"
$bookTradeId  = Get-SubCatId "二手交易" "教材转让"
$elecTradeId  = Get-SubCatId "二手交易" "电子产品"

Write-Host "`n>>> 创建任务..." -ForegroundColor Green

# 3. 以不同学生身份创建任务
Login-User "zhangsan" "pass123"
$t1 = Create-Task "求高数期末辅导" "微积分下册，求大佬带飞，报酬可议" $gaoshuId 50.00 0 7
$t2 = Create-Task "代取韵达快递" "韵达快递站在北门，送到3号宿舍楼" $packageId 5.00 1 1

Login-User "lisi" "pass123"
$t3 = Create-Task "急！电脑蓝屏求助" "笔记本开机蓝屏，系统是Win11" $repairId 30.00 1 2
$t4 = Create-Task "转让高数教材" "同济版高数下册，9成新，原价45" $bookTradeId 25.00 0 14

Login-User "wangwu" "pass123"
$t5 = Create-Task "食堂代购午餐" "二食堂三楼麻辣香锅，送到图书馆" $canteenId 8.00 0 0
$t6 = Create-Task "打印复习资料" "50页A4双面打印+装订" $printId 10.00 0 3

Login-User "zhaoliu" "pass123"
$t7 = Create-Task "英语四级写作辅导" "求英语好的同学指导四级写作" $englishId 40.00 0 5
$t8 = Create-Task "出售二手iPad" "iPad 9代 2021款，64G，带壳" $elecTradeId 1800.00 0 30

Login-User "sunqi" "pass123"
$t9 = Create-Task "Java作业辅导" "Spring Boot期末大作业辅导" $codingId 100.00 0 10

Write-Host "`n>>> 接单 & 完成任务..." -ForegroundColor Green

# 4. 学生互相接单
Login-User "lisi" "pass123"
Accept-Task $t1   # 李四接张三的高数辅导

Login-User "wangwu" "pass123"
Accept-Task $t3   # 王五接李四的电脑维修

Login-User "zhaoliu" "pass123"
Accept-Task $t5   # 赵六接王五的食堂代购
Accept-Task $t2   # 赵六接张三的取快递

Login-User "sunqi" "pass123"
Accept-Task $t6   # 孙七接王五的打印资料

# 5. 完成部分任务
Login-User "wangwu" "pass123"
Complete-Task $t3  # 王五完成电脑维修

Login-User "zhaoliu" "pass123"
Complete-Task $t5  # 赵六完成食堂代购

Write-Host "`n========== 种子数据已完成 ==========" -ForegroundColor Cyan

# 6. 验证
Write-Host "`n>>> 验证数据..." -ForegroundColor Yellow
Login-User "admin" "admin123"
$stats = Invoke-RestMethod -Uri "$BASE/dashboard/stats" -Method GET -Headers (Headers)
Write-Host "控制台统计: $($stats | ConvertTo-Json -Depth 3)"
$recent = Invoke-RestMethod -Uri "$BASE/dashboard/recent-tasks" -Method GET -Headers (Headers)
Write-Host "最新任务数: $($recent.data.Count)"
