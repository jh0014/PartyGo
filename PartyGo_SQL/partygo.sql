/*
Navicat MySQL Data Transfer

Source Server         : Mine
Source Server Version : 50710
Source Host           : localhost:3306
Source Database       : partygo

Target Server Type    : MYSQL
Target Server Version : 50710
File Encoding         : 65001

Date: 2018-04-01 20:22:06
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for pg_abs
-- ----------------------------
DROP TABLE IF EXISTS `pg_abs`;
CREATE TABLE `pg_abs` (
  `PARTYID` varchar(32) NOT NULL,
  `OPENID` varchar(32) NOT NULL,
  `PARTY_TITLE` varchar(255) NOT NULL,
  `APPID` varchar(32) NOT NULL,
  `CREATE_TIME` date NOT NULL DEFAULT '0000-00-00',
  `UPDATE_TIME` date NOT NULL DEFAULT '0000-00-00',
  `UPDATE_PERSON` varchar(32) NOT NULL DEFAULT '',
  `PARTY_TIME` date NOT NULL,
  `STATUS` int(1) NOT NULL DEFAULT '1'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for pg_detail
-- ----------------------------
DROP TABLE IF EXISTS `pg_detail`;
CREATE TABLE `pg_detail` (
  `PARTYID` varchar(32) NOT NULL,
  `PARTY_TITLE` varchar(255) NOT NULL,
  `PARTY_TIME` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' ON UPDATE CURRENT_TIMESTAMP,
  `PARTY_ADDRESS` varchar(1280) NOT NULL,
  `PARTY_SOUND` varchar(1280) DEFAULT NULL,
  `PARTY_IMG` varchar(1280) DEFAULT NULL,
  `CREATE_TIME` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' ON UPDATE CURRENT_TIMESTAMP,
  `UPDATE_TIME` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`PARTYID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for pg_friend
-- ----------------------------
DROP TABLE IF EXISTS `pg_friend`;
CREATE TABLE `pg_friend` (
  `APPID` varchar(32) NOT NULL,
  `OPENID1` varchar(32) NOT NULL,
  `OPENID2` varchar(32) NOT NULL,
  `CREATE_TIME` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for pg_group
-- ----------------------------
DROP TABLE IF EXISTS `pg_group`;
CREATE TABLE `pg_group` (
  `APPID` varchar(32) NOT NULL,
  `OPENID` varchar(32) NOT NULL,
  `GROUPID` varchar(32) NOT NULL,
  `CREATE_TIME` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' ON UPDATE CURRENT_TIMESTAMP,
  `UPDATE_TIME` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for pg_statis
-- ----------------------------
DROP TABLE IF EXISTS `pg_statis`;
CREATE TABLE `pg_statis` (
  `APINAME` varchar(32) NOT NULL,
  `COUNT` int(11) NOT NULL,
  `CREATE_TIME` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' ON UPDATE CURRENT_TIMESTAMP,
  `UPDATE_TIME` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`APINAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for pg_user
-- ----------------------------
DROP TABLE IF EXISTS `pg_user`;
CREATE TABLE `pg_user` (
  `APPID` varchar(32) NOT NULL,
  `OPENID` varchar(32) NOT NULL,
  `UNIONID` varchar(32) NOT NULL,
  `NICKNAME` varchar(255) NOT NULL,
  `IMAGE` varchar(1280) NOT NULL,
  `PHONENO` varchar(16) DEFAULT NULL,
  `CREATE_TIME` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' ON UPDATE CURRENT_TIMESTAMP,
  `UPDATE_TIME` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
