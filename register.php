<?php

require_once 'db.php';

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    jsonResponse(['success' => false, 'message' => 'Method not allowed'], 405);
}

$body     = json_decode(file_get_contents('php://input'), true);
$name     = trim($body['name']     ?? '');
$email    = trim($body['email']    ?? '');
$password = trim($body['password'] ?? '');

if ($name === '' || $email === '' || $password === '') {
    jsonResponse(['success' => false, 'message' => 'Field name, email, dan password wajib diisi'], 400);
}

if (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
    jsonResponse(['success' => false, 'message' => 'Format email tidak valid'], 400);
}

$db = getDB();

$check = $db->prepare('SELECT id FROM users WHERE email = ? LIMIT 1');
$check->execute([$email]);

if ($check->fetch()) {
    jsonResponse(['success' => false, 'message' => 'Email sudah terdaftar'], 409);
}

$stmt = $db->prepare(
    'INSERT INTO users (name, email, password, created_at, updated_at) VALUES (?, ?, ?, NOW(), NOW())'
);
$stmt->execute([$name, $email, password_hash($password, PASSWORD_DEFAULT)]);
$newId = (int) $db->lastInsertId();

jsonResponse([
    'success'  => true,
    'user_id'  => $newId,
    'username' => $name,
]);
