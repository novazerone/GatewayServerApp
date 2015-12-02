-- phpMyAdmin SQL Dump
-- version 4.4.14
-- http://www.phpmyadmin.net
--
-- Host: 127.0.0.1
-- Generation Time: Dec 02, 2015 at 02:23 PM
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
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=latin1;

--
-- Dumping data for table `files`
--

INSERT INTO `files` (`id`, `file_name`, `file_size`, `upload_datetime`, `status`) VALUES
(1, 'new_files', 100, '2015-11-30 08:36:45', 0),
(2, 'new_files2', 100, '2015-11-30 08:36:57', 0),
(3, 'Sample1', 1234, '2015-12-02 04:59:06', 1),
(10, 'file_11', 123, '2015-12-02 06:18:32', 0),
(11, 'file_113331133', 123, '2015-12-02 06:40:04', 0),
(12, 'file_new12', 123, '2015-12-02 06:41:28', 0),
(13, 'file_new123', 123, '2015-12-02 06:42:23', 0),
(14, 'file_new1233', 123, '2015-12-02 06:45:34', 0),
(15, 'file_new12334', 123, '2015-12-02 06:46:01', 0);

-- --------------------------------------------------------

--
-- Table structure for table `servers`
--

CREATE TABLE IF NOT EXISTS `servers` (
  `id` int(10) NOT NULL,
  `name` varchar(100) NOT NULL,
  `status` tinyint(1) NOT NULL,
  `total_file_size` int(11) NOT NULL,
  `port` int(11) NOT NULL
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=latin1;

--
-- Dumping data for table `servers`
--

INSERT INTO `servers` (`id`, `name`, `status`, `total_file_size`, `port`) VALUES
(1, 'server_1', 1, 100, 1),
(2, 'server_2', 1, 1000, 2),
(3, 'server_3', 1, 10000, 3),
(4, 'server_4', 1, 100, 4),
(5, 'server_5', 1, 1000, 5),
(6, 'server_6', 0, 100000, 6),
(7, 'server_7', 1, 10, 7);

-- --------------------------------------------------------

--
-- Table structure for table `server_file`
--

CREATE TABLE IF NOT EXISTS `server_file` (
  `id` int(11) NOT NULL,
  `file_id` int(11) NOT NULL,
  `server_id` int(11) NOT NULL,
  `status` int(11) NOT NULL
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=latin1;

--
-- Dumping data for table `server_file`
--

INSERT INTO `server_file` (`id`, `file_id`, `server_id`, `status`) VALUES
(2, 15, 7, 1),
(3, 15, 1, 1),
(4, 15, 4, 0),
(5, 15, 2, 0);

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
  ADD KEY `fk_file_server_server_id_servers_id` (`server_id`),
  ADD KEY `fk_file_server_file_id_files_id` (`file_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `files`
--
ALTER TABLE `files`
  MODIFY `id` int(10) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=17;
--
-- AUTO_INCREMENT for table `servers`
--
ALTER TABLE `servers`
  MODIFY `id` int(10) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=8;
--
-- AUTO_INCREMENT for table `server_file`
--
ALTER TABLE `server_file`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=6;
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
