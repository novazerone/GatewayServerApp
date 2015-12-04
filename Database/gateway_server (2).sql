-- phpMyAdmin SQL Dump
-- version 4.4.14
-- http://www.phpmyadmin.net
--
-- Host: 127.0.0.1
-- Generation Time: Dec 04, 2015 at 09:31 AM
-- Server version: 5.6.26
-- PHP Version: 5.6.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `gateway_server`
--

-- --------------------------------------------------------

--
-- Table structure for table `files`
--

CREATE TABLE IF NOT EXISTS `files` (
  `id` int(10) NOT NULL,
  `file_name` varchar(100) NOT NULL,
  `file_size` int(11) NOT NULL,
  `upload_datetime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `status` int(11) NOT NULL
) ENGINE=InnoDB AUTO_INCREMENT=36 DEFAULT CHARSET=latin1;

--
-- Dumping data for table `files`
--

INSERT INTO `files` (`id`, `file_name`, `file_size`, `upload_datetime`, `status`) VALUES
(32, 'GatewayServerApp.eml', 365, '2015-12-03 09:54:44', 0),
(33, 'gateway_server_process.txt', 1682, '2015-12-03 12:39:28', 0),
(34, 'gateway_server.sql', 4386, '2015-12-03 12:51:11', 0),
(35, 'README.md', 19, '2015-12-03 12:58:37', 0);

-- --------------------------------------------------------

--
-- Table structure for table `servers`
--

CREATE TABLE IF NOT EXISTS `servers` (
  `id` int(10) NOT NULL,
  `name` varchar(100) NOT NULL,
  `status` tinyint(1) NOT NULL,
  `total_file_size` int(11) NOT NULL,
  `port` int(11) NOT NULL,
  `download_port` int(11) NOT NULL
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=latin1;

--
-- Dumping data for table `servers`
--

INSERT INTO `servers` (`id`, `name`, `status`, `total_file_size`, `port`, `download_port`) VALUES
(16, '1', 0, 626, 8082, 0),
(17, '2', 0, 3364, 8084, 0),
(18, '3', 0, 8772, 8086, 0);

-- --------------------------------------------------------

--
-- Table structure for table `server_file`
--

CREATE TABLE IF NOT EXISTS `server_file` (
  `id` int(11) NOT NULL,
  `file_id` int(11) NOT NULL,
  `server_id` int(11) NOT NULL,
  `status` int(11) NOT NULL
) ENGINE=InnoDB AUTO_INCREMENT=76 DEFAULT CHARSET=latin1;

--
-- Dumping data for table `server_file`
--

INSERT INTO `server_file` (`id`, `file_id`, `server_id`, `status`) VALUES
(68, 32, 16, 2),
(69, 32, 17, 2),
(70, 33, 17, 2),
(71, 33, 18, 2),
(72, 34, 18, 2),
(73, 34, 16, 2),
(74, 35, 16, 2),
(75, 35, 17, 2);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `files`
--
ALTER TABLE `files`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `file_name` (`file_name`);

--
-- Indexes for table `servers`
--
ALTER TABLE `servers`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `unique_server_name` (`name`),
  ADD UNIQUE KEY `port` (`port`);

--
-- Indexes for table `server_file`
--
ALTER TABLE `server_file`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `unique_index` (`server_id`,`file_id`),
  ADD KEY `fk_file_server_file_id_files_id` (`file_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `files`
--
ALTER TABLE `files`
  MODIFY `id` int(10) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=36;
--
-- AUTO_INCREMENT for table `servers`
--
ALTER TABLE `servers`
  MODIFY `id` int(10) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=19;
--
-- AUTO_INCREMENT for table `server_file`
--
ALTER TABLE `server_file`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=76;
--
-- Constraints for dumped tables
--

--
-- Constraints for table `server_file`
--
ALTER TABLE `server_file`
  ADD CONSTRAINT `fk_file_server_file_id_files_id` FOREIGN KEY (`file_id`) REFERENCES `files` (`id`),
  ADD CONSTRAINT `fk_file_server_server_id_servers_id` FOREIGN KEY (`server_id`) REFERENCES `servers` (`id`);

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
