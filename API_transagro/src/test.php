<?php
$dsn = 'mysql:host=db;dbname=transagro_db;charset=utf8';
$username = 'root';
$password = 'root';

try {
    $pdo = new PDO($dsn, $username, $password);
    echo "Connexion réussie via PDO!";
} catch (PDOException $e) {
    echo "Erreur de connexion PDO : " . $e->getMessage();
}
?>

