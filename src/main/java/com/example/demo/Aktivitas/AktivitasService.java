package com.example.demo.Aktivitas;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;

@Service
public class AktivitasService {
  @Autowired
  private AktivitasRepository aktivitasRepository;

  public void tambahAktivitas(Aktivitas aktivitas) {
    aktivitasRepository.insertAktivitas(aktivitas);
  }

  public List<Aktivitas> getAktivitasByUserId(Integer userId) {
    return aktivitasRepository.findAktivitasByUserId(userId);
  }

  public Aktivitas getAktivitasById(Integer idAktivitas, Integer idUser) {
    return aktivitasRepository.getAktivitasById(idAktivitas, idUser);
  }

  public void updateAktivitas(Aktivitas aktivitas, Integer idUser) {
    aktivitasRepository.updateAktivitas(aktivitas, idUser);
  }

  public void deleteAktivitas(Integer idAktivitas, Integer idUser) {
    aktivitasRepository.deleteAktivitas(idAktivitas, idUser);
  }

  public void deleteFotoByUrl(String urlFoto) {
    aktivitasRepository.deleteFotoByUrl(urlFoto);
  }

  public List<String> uploadFoto(MultipartFile[] files, Integer idAktivitas) throws IOException {
    if (files == null || files.length == 0) {
      throw new IllegalArgumentException("Tidak ada file yang diunggah!");
    }

    List<String> urls = new ArrayList<>();
    for (MultipartFile file : files) {
      if (!file.isEmpty()) {
        // Generate unique file name
        String originalName = file.getOriginalFilename();
        String extension = originalName.substring(originalName.lastIndexOf("."));
        String uniqueFileName = UUID.randomUUID().toString() + extension;

        // Save the file
        String uploadDir = "src/main/resources/static/uploads/aktivitas/";
        Path filePath = Paths.get(uploadDir + uniqueFileName);
        Files.createDirectories(filePath.getParent());
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // Save URL to database
        aktivitasRepository.insertFoto(idAktivitas, uniqueFileName);
        urls.add(uniqueFileName);
      }
    }
    return urls;
  }
}
