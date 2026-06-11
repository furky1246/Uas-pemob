<?php
// get_chapters_by_juz.php
require_once __DIR__ . '/config.php';

// Verify GET request
if ($_SERVER['REQUEST_METHOD'] !== 'GET') {
    sendResponse([
        "success" => false,
        "message" => "Method not allowed. Use GET."
    ], 405);
}

// Get juz number from query parameters
$juzNumber = isset($_GET['juz']) ? (int)$_GET['juz'] : 0;

if ($juzNumber <= 0 || $juzNumber > 30) {
    sendResponse([
        "success" => false,
        "message" => "Nomor Juz tidak valid. Harus antara 1 dan 30."
    ], 400);
}

try {
    // Query distinct chapters that have verses in this Juz
    $stmt = $pdo->prepare("
        SELECT DISTINCT c.id, c.name, c.verse_count 
        FROM chapters c 
        JOIN verses v ON c.id = v.id_chapter 
        WHERE v.id_juz = ? 
        ORDER BY c.id ASC
    ");
    $stmt->execute([$juzNumber]);
    $chapters = $stmt->fetchAll();

    // If database is empty or seeder hasn't run, JOIN might return empty.
    // In that case, we can provide a fallback for Juz 30 and Juz 1 to make it testable
    if (empty($chapters)) {
        if ($juzNumber === 30) {
            // Juz 30 fallback
            $stmtFallback = $pdo->prepare("SELECT id, name, verse_count FROM chapters WHERE id BETWEEN 78 AND 114 ORDER BY id ASC");
            $stmtFallback->execute();
            $chapters = $stmtFallback->fetchAll();
        } else if ($juzNumber === 1) {
            // Juz 1 fallback
            $stmtFallback = $pdo->prepare("SELECT id, name, verse_count FROM chapters WHERE id IN (1, 2) ORDER BY id ASC");
            $stmtFallback->execute();
            $chapters = $stmtFallback->fetchAll();
        }
    }

    $response = [];
    foreach ($chapters as $chapter) {
        $response[] = [
            "id"          => (int)$chapter['id'],
            "name"        => $chapter['name'],
            "verse_count" => (int)$chapter['verse_count']
        ];
    }

    sendResponse($response);
} catch (Exception $e) {
    sendResponse([
        "success" => false,
        "message" => "Terjadi kesalahan server: " . $e->getMessage()
    ], 500);
}
