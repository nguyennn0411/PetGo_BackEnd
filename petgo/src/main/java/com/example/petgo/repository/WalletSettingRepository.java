package com.example.petgo.repository;

import com.example.petgo.entity.WalletSetting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WalletSettingRepository extends JpaRepository<WalletSetting, Long> {
    Optional<WalletSetting> findBySettingKey(String settingKey);
}