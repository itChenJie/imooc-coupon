-- 登录 MySQL 服务器
mysql -h49.233.201.120:8889 -uadmin -p123456
-- 创建数据库 imooc_coupon_data
CREATE DATABASE IF NOT EXISTS imooc_coupon_data;

-- 登录 Mysql 服务器，并进入到 imooc_coupon_data 数据库中
mysql -h49.233.201.120:8889 -uadmin -p123456 -Dimooc_coupon_data