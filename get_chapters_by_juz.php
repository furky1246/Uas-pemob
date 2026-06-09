<?php

require_once 'db.php';

if ($_SERVER['REQUEST_METHOD'] !== 'GET') {
    jsonResponse(['success' => false, 'message' => 'Method not allowed'], 405);
}

$juz = filter_input(INPUT_GET, 'juz', FILTER_VALIDATE_INT);

if (!$juz || $juz < 1 || $juz > 30) {
    jsonResponse(['success' => false, 'message' => 'Parameter juz tidak valid (1-30)'], 400);
}

$db = getDB();

// chapters tidak punya kolom id_juz.
// Mapping dilakukan via verses: ambil distinct id_chapter dari verses
// berdasarkan id_juz, lalu JOIN ke chapters untuk mendapat detail surah.
$stmt = $db->prepare('
    SELECT DISTINCT c.id, c.name, c.verse_count
    FROM verses v
    JOIN chapters c ON c.id = v.id_chapter
    WHERE v.id_juz = ?
    ORDER BY c.id ASC
');
$stmt->execute([$juz]);
$rows = $stmt->fetchAll();

if (empty($rows)) {
    jsonResponse(['success' => false, 'message' => 'Juz tidak ditemukan'], 404);
}

$chapters = array_map(fn($row) => [
    'id'          => (int) $row['id'],
    'name'        => $row['name'],
    'verse_count' => (int) $row['verse_count'],
], $rows);

jsonResponse($chapters);
