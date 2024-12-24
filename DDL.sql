CREATE TYPE role_user AS ENUM ('admin', 'member');

CREATE TABLE Users (
  id_user SERIAL PRIMARY KEY,
  nama_user VARCHAR(255) NOT NULL,
  email VARCHAR(255) NOT NULL UNIQUE,
  password VARCHAR(255) NOT NULL,
  role_user VARCHAR(100) NOT NULL DEFAULT 'member'
);

CREATE TYPE satuan_jarak AS ENUM ('kilometer', 'meter', 'mil', 'yard');

CREATE TABLE Aktivitas (
    id_aktivitas SERIAL PRIMARY KEY,
    tanggal_aktivitas DATE NOT NULL,
    judul VARCHAR(255) NOT NULL,
    deskripsi TEXT,
    waktu_tempuh TIME NOT NULL,
    jarak_tempuh NUMERIC(10, 2),
    satuan_jarak VARCHAR(100) NOT NULL,
    id_user INT NOT NULL,
    FOREIGN KEY (id_user) REFERENCES Users (id_user) ON DELETE CASCADE
);

CREATE TABLE Foto_Aktivitas (
    id_foto SERIAL PRIMARY KEY,
    id_aktivitas INT NOT NULL,
    url_foto VARCHAR(255) NOT NULL,
    FOREIGN KEY (id_aktivitas) REFERENCES Aktivitas (id_aktivitas) ON DELETE CASCADE
);

CREATE TABLE Lomba (
    id_lomba SERIAL PRIMARY KEY,
    nama_lomba VARCHAR(255) NOT NULL,
    deskripsi_lomba TEXT
);

CREATE TABLE Lomba_Member (
    id_lomba INT NOT NULL,
    id_user INT NOT NULL,
    id_aktivitas INT UNIQUE,
    PRIMARY KEY (id_lomba, id_user),
    FOREIGN KEY (id_lomba) REFERENCES Lomba(id_lomba),
    FOREIGN KEY (id_user) REFERENCES Users(id_user)
);
