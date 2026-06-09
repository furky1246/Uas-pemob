<?php

require_once 'db.php';

if ($_SERVER['REQUEST_METHOD'] !== 'GET') {
    jsonResponse(['success' => false, 'message' => 'Method not allowed'], 405);
}

$userId = filter_input(INPUT_GET, 'user_id', FILTER_VALIDATE_INT);

if (!$userId || $userId < 1) {
    jsonResponse(['success' => false, 'message' => 'Parameter user_id tidak valid'], 400);
}

$db   = getDB();
$stmt = $db->prepare('SELECT id, name, email FROM users WHERE id = ? LIMIT 1');
$stmt->execute([$userId]);
$user = $stmt->fetch();

if (!$user) {
    jsonResponse(['success' => false, 'message' => 'User tidak ditemukan'], 404);
}

jsonResponse([
    'id'    => (int) $user['id'],
    'name'  => $user['name'],
    'email' => $user['email'],
]);
