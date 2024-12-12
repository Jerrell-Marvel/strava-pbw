CREATE TABLE Member (
    id_member SERIAL PRIMARY KEY,
    nama_member VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE Admin (
    id_admin SERIAL PRIMARY KEY,
    nama_admin VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

CREATE TYPE satuan_jarak AS ENUM ('kilometer', 'meter', 'mil', 'yard');

CREATE TABLE Aktivitas (
    id_aktivitas SERIAL PRIMARY KEY,
    tanggal_aktivitas DATE NOT NULL,
    judul VARCHAR(255) NOT NULL,
    deskripsi TEXT,
    waktu_tempuh INTERVAL NOT NULL,
    jarak_tempuh NUMERIC(10, 2),
    satuan_jarak satuan_jarak NOT NULL,
    id_member INT NOT NULL,
    FOREIGN KEY (id_member) REFERENCES Member (id_member) ON DELETE CASCADE
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
    id_member INT NOT NULL,
    id_aktivitas INT NOT NULL UNIQUE,
    PRIMARY KEY (id_lomba, id_member),
    FOREIGN KEY (id_lomba) REFERENCES Lomba(id),
    FOREIGN KEY (id_member) REFERENCES Member(id_member)
);
