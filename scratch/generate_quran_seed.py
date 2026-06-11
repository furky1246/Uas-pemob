import urllib.request
import json
import os
import sys

# Define target paths
SCRATCH_DIR = os.path.dirname(os.path.abspath(__file__))
WORKSPACE_DIR = os.path.dirname(SCRATCH_DIR)
OUTPUT_SQL_PATH = os.path.join(WORKSPACE_DIR, 'api_sambungayat', 'database.sql')

# Make sure output directory exists
os.makedirs(os.path.dirname(OUTPUT_SQL_PATH), exist_ok=True)

print("Starting Quran Database Seed Generator (Aligned with db_quran_emufassir)...")

# Base SQL Schema matching db_quran_emufassir structures
sql_content = """-- Database: db_quran_emufassir
CREATE DATABASE IF NOT EXISTS db_quran_emufassir;
USE db_quran_emufassir;

SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS user_progress;
DROP TABLE IF EXISTS verse_translations;
DROP TABLE IF EXISTS verse_audios;
DROP TABLE IF EXISTS verses;
DROP TABLE IF EXISTS chapters;
DROP TABLE IF EXISTS juzs;
DROP TABLE IF EXISTS users;
SET FOREIGN_KEY_CHECKS = 1;

-- Table users
CREATE TABLE users (
  id bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  name varchar(255) NOT NULL,
  email varchar(255) NOT NULL UNIQUE,
  email_verified_at timestamp NULL DEFAULT NULL,
  password varchar(255) NOT NULL,
  remember_token varchar(100) DEFAULT NULL,
  created_at timestamp NULL DEFAULT NULL,
  updated_at timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table juzs
CREATE TABLE juzs (
  id int(11) NOT NULL PRIMARY KEY,
  name varchar(255) NOT NULL,
  number int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Table chapters
CREATE TABLE chapters (
  id int(11) NOT NULL PRIMARY KEY,
  name varchar(255) NOT NULL,
  number_chapter int(11) NOT NULL,
  arabic_name text NOT NULL,
  revelation_order int(11) NOT NULL,
  revelation_place varchar(10) NOT NULL,
  verse_count int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Table verses
CREATE TABLE verses (
  id int(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,
  text_uthmani text DEFAULT NULL,
  text_uthmani_simple text DEFAULT NULL,
  text_imlaei text DEFAULT NULL,
  text_imlaei_simple text DEFAULT NULL,
  text_indopak text DEFAULT NULL,
  number int(11) NOT NULL,
  transliteration text NOT NULL,
  unicode_uthmani text DEFAULT NULL,
  unicode_uthmani_simple text DEFAULT NULL,
  unicode_imlaei text DEFAULT NULL,
  unicode_imlaei_simple text DEFAULT NULL,
  unicode_indopak text DEFAULT NULL,
  id_juz int(11) NOT NULL,
  id_chapter int(11) NOT NULL,
  FOREIGN KEY (id_chapter) REFERENCES chapters(id) ON DELETE CASCADE,
  FOREIGN KEY (id_juz) REFERENCES juzs(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Table verse_translations
CREATE TABLE verse_translations (
  id int(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,
  text text NOT NULL,
  id_translation int(11) NOT NULL,
  id_verse int(11) NOT NULL,
  FOREIGN KEY (id_verse) REFERENCES verses(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Table verse_audios
CREATE TABLE verse_audios (
  id int(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,
  url text NOT NULL,
  id_verse int(11) NOT NULL,
  id_recitation int(11) NOT NULL,
  FOREIGN KEY (id_verse) REFERENCES verses(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Table user_progress
CREATE TABLE user_progress (
  id int(11) UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  user_id bigint(20) UNSIGNED NOT NULL,
  total_score int(11) NOT NULL DEFAULT 0,
  best_streak int(11) NOT NULL DEFAULT 0,
  highest_unlocked_surah int(11) NOT NULL DEFAULT 1,
  current_surah int(11) NOT NULL DEFAULT 1,
  current_verse int(11) NOT NULL DEFAULT 1,
  updated_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

"""

def escape_sql(val):
    if val is None:
        return 'NULL'
    return "'" + str(val).replace("'", "''") + "'"

# 2. Generate Juz Data
print("Generating Juz data...")
sql_content += "-- Seeding Juzs\n"
for i in range(1, 31):
    sql_content += f"INSERT INTO juzs (id, name, number) VALUES ({i}, 'Juz {i}', {i});\n"
sql_content += "\n"

# 3. Generate Chapters Metadata from API
print("Fetching Surah metadata from API...")
try:
    url = "https://api.alquran.cloud/v1/meta"
    req = urllib.request.Request(url, headers={'User-Agent': 'Mozilla/5.0'})
    with urllib.request.urlopen(req) as response:
        meta_data = json.loads(response.read().decode('utf-8'))
        surahs = meta_data['data']['surahs']['references']
        
        sql_content += "-- Seeding Chapters (Surah)\n"
        for surah in surahs:
            s_id = surah['number']
            s_name = surah['englishName']
            s_verses = surah['numberOfAyahs']
            s_arabic = surah['name']
            s_revelation_type = surah['revelationType'].lower()
            
            # Use placeholders for order as the simple metadata has it differently
            sql_content += f"INSERT INTO chapters (id, name, number_chapter, arabic_name, revelation_order, revelation_place, verse_count) " \
                           f"VALUES ({s_id}, {escape_sql(s_name)}, {s_id}, {escape_sql(s_arabic)}, {s_id}, {escape_sql(s_revelation_type)}, {s_verses});\n"
        sql_content += "\n"
except Exception as e:
    print(f"Error fetching surah metadata: {e}")
    sys.exit(1)

# 4. Fetch Verses for Surah 1 and Surah 78-114 (Juz 30)
surahs_to_fetch = [1] + list(range(78, 115))
sql_content += "-- Seeding Verses, Translations, and Audios\n"

verse_counter = 1
translation_counter = 1
audio_counter = 1

for s_num in surahs_to_fetch:
    print(f"Fetching verses for Surah {s_num}...")
    try:
        url = f"https://api.alquran.cloud/v1/surah/{s_num}/editions/quran-simple,id.indonesian,ar.alafasy"
        req = urllib.request.Request(url, headers={'User-Agent': 'Mozilla/5.0'})
        with urllib.request.urlopen(req) as response:
            res_data = json.loads(response.read().decode('utf-8'))
            
            editions = res_data['data']
            arabic_edition = editions[0]['ayahs']
            translation_edition = editions[1]['ayahs']
            audio_edition = editions[2]['ayahs']
            
            for i in range(len(arabic_edition)):
                v_num = arabic_edition[i]['numberInSurah']
                arabic_text = arabic_edition[i]['text']
                translation_text = translation_edition[i]['text']
                audio_url = audio_edition[i]['text']
                juz_id = arabic_edition[i]['juz']
                
                simple_text = arabic_text.replace("بسم الله الرحمن الرحيم", "")
                if not simple_text.strip():
                    simple_text = arabic_text
                
                # Insert verse
                sql_content += f"INSERT INTO verses (id, text_uthmani, text_uthmani_simple, number, transliteration, id_juz, id_chapter) VALUES " \
                               f"({verse_counter}, {escape_sql(arabic_text)}, {escape_sql(simple_text.strip())}, {v_num}, 'verse_{s_num}_{v_num}', {juz_id}, {s_num});\n"
                
                # Insert translation (ID 33 represents Indonesian)
                sql_content += f"INSERT INTO verse_translations (id, text, id_translation, id_verse) VALUES " \
                               f"({translation_counter}, {escape_sql(translation_text)}, 33, {verse_counter});\n"
                
                # Insert audio (ID 1 represents Reciter 1)
                sql_content += f"INSERT INTO verse_audios (id, url, id_verse, id_recitation) VALUES " \
                               f"({audio_counter}, {escape_sql(audio_url)}, {verse_counter}, 1);\n"
                
                verse_counter += 1
                translation_counter += 1
                audio_counter += 1
            
            sql_content += "\n"
    except Exception as e:
        print(f"Error fetching Surah {s_num}: {e}")
        sys.exit(1)

# Write to file
try:
    with open(OUTPUT_SQL_PATH, 'w', encoding='utf-8') as f:
        f.write(sql_content)
    print(f"Success! SQL file generated at {OUTPUT_SQL_PATH}")
except Exception as e:
    print(f"Error writing SQL file: {e}")
    sys.exit(1)
