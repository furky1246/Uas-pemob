<?php
// get_verses.php
require_once __DIR__ . '/config.php';

// Verify GET request
if ($_SERVER['REQUEST_METHOD'] !== 'GET') {
    sendResponse([
        "success" => false,
        "message" => "Method not allowed. Use GET."
    ], 405);
}

// Get chapter ID from query parameters
$chapterId = isset($_GET['chapter_id']) ? (int)$_GET['chapter_id'] : 0;

if ($chapterId <= 0 || $chapterId > 114) {
    sendResponse([
        "success" => false,
        "message" => "Chapter ID tidak valid. Harus antara 1 dan 114."
    ], 400);
}

try {
    // Query verses for the game (requires only verse number and Arabic text)
    $stmt = $pdo->prepare("SELECT number AS verse_number, text_uthmani AS text FROM verses WHERE id_chapter = ? ORDER BY number ASC");
    $stmt->execute([$chapterId]);
    $verses = $stmt->fetchAll();

    $response = [];
    foreach ($verses as $verse) {
        $response[] = [
            "verse_number" => (int)$verse['verse_number'],
            "text"         => $verse['text']
        ];
    }

    sendResponse($response);
} catch (Exception $e) {
    sendResponse([
        "success" => false,
        "message" => "Terjadi kesalahan server: " . $e->getMessage()
    ], 500);
}
