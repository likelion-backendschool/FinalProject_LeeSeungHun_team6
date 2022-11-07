# 테스트 DB 생성
DROP DATABASE IF EXISTS reader_test;
CREATE DATABASE reader_test;
USE reader_test;

# 개발 DB 생성
DROP DATABASE IF EXISTS reader_dev;
CREATE DATABASE reader_dev;
USE reader_dev;

# 운영 DB 생성
DROP DATABASE IF EXISTS reader_prod;
CREATE DATABASE reader_prod;
USE reader_prod;