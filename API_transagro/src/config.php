<?php
// config.php
$dsn = 'mysql:host=db;dbname=transagro_db;charset=utf8';
$username = 'root';
$password = 'root';

$options = [
    PDO::ATTR_ERRMODE            => PDO::ERRMODE_EXCEPTION,
    PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC,
    PDO::ATTR_EMULATE_PREPARES   => false,
];

try {
    $pdo = new PDO($dsn, $username, $password, $options);
} catch (PDOException $e) {
    // Retourne une erreur en JSON
    die(json_encode(["error" => "La connexion à la base de données a échoué : " . $e->getMessage()]));
}
?>
