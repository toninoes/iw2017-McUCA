--
-- Crear base de datos: `iw2017McUCA`
--
CREATE DATABASE IF NOT EXISTS `iw2017McUCA` DEFAULT CHARACTER SET latin1 COLLATE latin1_swedish_ci;

--
-- Damos todos los privilegios al usuario: iw2017McUCA con clave: iw2017McUCA sobre BBDD: iw2017McUCA
-- Si el usuario no existe lo creará
--
GRANT ALL ON `iw2017McUCA`.* TO 'iw2017McUCA'@'localhost' IDENTIFIED BY 'iw2017McUCA';

USE `iw2017McUCA`;
