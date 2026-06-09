<?php

require_once 'db.php';

if ($_SERVER['REQUEST_METHOD'] !== 'GET') {
    jsonResponse(['success' => false, 'message' => 'Method not allowed'], 405);
}

$db   = getDB();
$stmt = $db->query('SELECT id, name, verse_count FROM chapters ORDER BY id ASC');
$rows = $stmt->fetchAll();

$chapters = array_map(fn($row) => [
    'id'          => (int) $row['id'],
    'name'        => $row['name'],
    'verses_count' => (int) $row['verse_count'],
], $rows);

jsonResponse($chapters);
