-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Servidor: 127.0.0.1
-- Tiempo de generación: 02-11-2025 a las 22:25:26
-- Versión del servidor: 10.4.32-MariaDB
-- Versión de PHP: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `spafinal_gp6`
--
CREATE DATABASE IF NOT EXISTS `spafinal_gp6` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `spafinal_gp6`;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `cliente`
--

CREATE TABLE `cliente` (
  `codCli` int(11) NOT NULL,
  `dni` bigint(11) NOT NULL,
  `nombre` varchar(60) NOT NULL,
  `telefono` bigint(11) NOT NULL,
  `edad` int(11) NOT NULL,
  `afecciones` varchar(60) NOT NULL,
  `estado` tinyint(1) NOT NULL DEFAULT 1,
  `fechaNac` date NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `consultorio`
--

CREATE TABLE `consultorio` (
  `nroConsultorio` int(11) NOT NULL,
  `usos` int(11) NOT NULL,
  `equipamiento` varchar(60) NOT NULL,
  `apto` varchar(60) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `dia_de_spa`
--

CREATE TABLE `dia_de_spa` (
  `codPack` int(11) NOT NULL,
  `fechayhoraCompra` datetime NOT NULL,
  `preferencias` varchar(60) NOT NULL,
  `codCli` int(11) NOT NULL,
  `monto` double NOT NULL,
  `estado` tinyint(4) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `empleado`
--

CREATE TABLE `empleado` (
  `idEmpleado` int(11) NOT NULL,
  `dni` int(11) NOT NULL,
  `nombre` varchar(60) NOT NULL,
  `apellido` varchar(60) NOT NULL,
  `telefono` varchar(20) DEFAULT NULL,
  `fechaNacimiento` date NOT NULL,
  `puesto` varchar(40) NOT NULL,
  `matricula` varchar(20) DEFAULT NULL,
  `especialidad` varchar(60) DEFAULT NULL,
  `usuario` VARCHAR(40) NOT NULL UNIQUE,
  `pass` VARCHAR(100) NOT NULL,
  `estado` tinyint(1) NOT NULL DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `instalacion`
--

CREATE TABLE `instalacion` (
  `codInstal` int(11) NOT NULL,
  `nombre` varchar(60) NOT NULL,
  `detalleUso` varchar(60) NOT NULL,
  `precio30min` double NOT NULL,
  `estado` tinyint(1) NOT NULL DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `sesion`
--

CREATE TABLE `sesion` (
  `codSesion` int(11) NOT NULL,
  `fechaHoraInicio` datetime NOT NULL,
  `fechaHoraFinal` datetime NOT NULL,
  `codTratam` int(11) NOT NULL,
  `nroConsultorio` int(11) NOT NULL,
  `matriculaMasajista` varchar(20) NOT NULL,
  `idRegistrador` int(11) NOT NULL,
  `codInstal` int(11) NOT NULL,
  `codPack` int(11) NOT NULL,
  `monto` double NOT NULL DEFAULT 0,
  `notas` varchar(200),  
  `estado` tinyint(1) NOT NULL DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `tratamiento`
--

CREATE TABLE `tratamiento` (
  `codTratam` int(11) NOT NULL,
  `nombre` varchar(60) NOT NULL,
  `tipo` varchar(200) NOT NULL,
  `detalle` varchar(60) NOT NULL,
  `productos` varchar(50) NOT NULL,
  `duracion` int(11) NOT NULL,
  `costo` double NOT NULL,
  `estado` tinyint(1) NOT NULL DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `cliente`
--
ALTER TABLE `cliente`
  ADD PRIMARY KEY (`codCli`),
  ADD UNIQUE KEY `dni` (`dni`);

--
-- Indices de la tabla `consultorio`
--
ALTER TABLE `consultorio`
  ADD PRIMARY KEY (`nroConsultorio`);

--
-- Indices de la tabla `dia_de_spa`
--
ALTER TABLE `dia_de_spa`
  ADD PRIMARY KEY (`codPack`),
  ADD KEY `fk_codCli` (`codCli`);

--
-- Indices de la tabla `empleado`
--
ALTER TABLE `empleado`
  ADD PRIMARY KEY (`idEmpleado`),
  ADD UNIQUE KEY `dni` (`dni`),
  ADD UNIQUE KEY `matricula` (`matricula`);

--
-- Indices de la tabla `instalacion`
--
ALTER TABLE `instalacion`
  ADD PRIMARY KEY (`codInstal`);

--
-- Indices de la tabla `sesion`
--
ALTER TABLE `sesion`
  ADD PRIMARY KEY (`codSesion`),
  ADD KEY `fk_codTratam` (`codTratam`),
  ADD KEY `fk_nroConsultorio` (`nroConsultorio`),
  ADD KEY `fk_matriculaMasajista` (`matriculaMasajista`),
  ADD KEY `fk_idRegistrador` (`idRegistrador`),
  ADD KEY `fk_codInstal` (`codInstal`),
  ADD KEY `fk_codPack` (`codPack`);

--
-- Indices de la tabla `tratamiento`
--
ALTER TABLE `tratamiento`
  ADD PRIMARY KEY (`codTratam`);

--
-- AUTO_INCREMENT de las tablas volcadas
--

--
-- AUTO_INCREMENT de la tabla `cliente`
--
ALTER TABLE `cliente`
  MODIFY `codCli` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- AUTO_INCREMENT de la tabla `dia_de_spa`
--
ALTER TABLE `dia_de_spa`
  MODIFY `codPack` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `empleado`
--
ALTER TABLE `empleado`
  MODIFY `idEmpleado` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `instalacion`
--
ALTER TABLE `instalacion`
  MODIFY `codInstal` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `sesion`
--
ALTER TABLE `sesion`
  MODIFY `codSesion` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `tratamiento`
--
ALTER TABLE `tratamiento`
  MODIFY `codTratam` int(11) NOT NULL AUTO_INCREMENT;

--
-- Restricciones para tablas volcadas
--

--
-- Filtros para la tabla `dia_de_spa`
--
ALTER TABLE `dia_de_spa`
  ADD CONSTRAINT `fk_codCli` FOREIGN KEY (`codCli`) REFERENCES `cliente` (`codCli`);

--
-- Filtros para la tabla `sesion`
--
ALTER TABLE `sesion`
  ADD CONSTRAINT `fk_codInstal` FOREIGN KEY (`codInstal`) REFERENCES `instalacion` (`codInstal`),
  ADD CONSTRAINT `fk_codPack` FOREIGN KEY (`codPack`) REFERENCES `dia_de_spa` (`codPack`),
  ADD CONSTRAINT `fk_codTratam` FOREIGN KEY (`codTratam`) REFERENCES `tratamiento` (`codTratam`),
  ADD CONSTRAINT `fk_idRegistrador` FOREIGN KEY (`idRegistrador`) REFERENCES `empleado` (`idEmpleado`),
  ADD CONSTRAINT `fk_matriculaMasajista` FOREIGN KEY (`matriculaMasajista`) REFERENCES `empleado` (`matricula`),
  ADD CONSTRAINT `fk_nroConsultorio` FOREIGN KEY (`nroConsultorio`) REFERENCES `consultorio` (`nroConsultorio`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
