<?php
// login.php
require_once __DIR__ . '/config.php';

// Verify POST request
if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    sendResponse([
        "success" => false,
        "message" => "Method not allowed. Use POST."
    ], 405);
}

// Get JSON input
$input = getJsonInput();
$email = isset($input['email']) ? trim($input['email']) : '';
$password = isset($input['password']) ? trim($input['password']) : '';

// Validation
if (empty($email) || empty($password)) {
    sendResponse([
        "success" => false,
        "user_id" => null,
        "username" => null,
        "email" => null,
        "message" => "Email dan password wajib diisi."
    ], 400);
}

try {
    // Find user by email
    $stmt = $pdo->prepare("SELECT id, name, email, password FROM users WHERE email = ?");
    $stmt->execute([$email]);
    $user = $stmt->fetch();

    if ($user && password_verify($password, $user['password'])) {
        // Successful login
        sendResponse([
            "success" => true,
            "user_id" => (int)$user['id'],
            "username" => $user['name'],
            "email" => $user['email'],
            "message" => "Login berhasil"
        ]);
    } else {
        // Failed login
        sendResponse([
            "success" => false,
            "user_id" => null,
            "username" => null,
            "email" => null,
            "message" => "Email atau password salah."
        ], 401);
    }
} catch (Exception $e) {
    sendResponse([
        "success" => false,
        "user_id" => null,
        "username" => null,
        "email" => null,
        "message" => "Terjadi kesalahan server: " . $e->getMessage()
    ], 500);
}
