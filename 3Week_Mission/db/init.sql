# 테스트 DB 생성
DROP DATABASE IF EXISTS market_test;
CREATE DATABASE market_test;
USE market_test;

# 개발 DB 생성
DROP DATABASE IF EXISTS market_dev;
CREATE DATABASE market_dev;
USE market_dev;

# 운영 DB 생성
DROP DATABASE IF EXISTS market_prod;
CREATE DATABASE market_prod;
USE market_prod;