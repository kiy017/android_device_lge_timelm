#
# SPDX-FileCopyrightText: The LineageOS Project
# SPDX-License-Identifier: Apache-2.0
#

# Inherit from those products. Most specific first.
$(call inherit-product, $(SRC_TARGET_DIR)/product/core_64_bit.mk)
$(call inherit-product, $(SRC_TARGET_DIR)/product/full_base_telephony.mk)

# Inherit from device
$(call inherit-product, device/lge/timelm/device.mk)
TARGET_DISABLE_EPPE := true
# Inherit some common Lineage stuff.
$(call inherit-product, vendor/lineage/config/common_full_phone.mk)

# Device identifier. This must come after all inclusions.
PRODUCT_NAME := lineage_timelm
PRODUCT_DEVICE := timelm
PRODUCT_MANUFACTURER := LGE
PRODUCT_BRAND := lge
PRODUCT_MODEL := LM-V600

PRODUCT_GMS_CLIENTID_BASE := android-lge

PRODUCT_BUILD_PROP_OVERRIDES += \
    DeviceName=timelm \
    BuildDesc="timelm-user 13 TKQ1.220829.002 2318902104908 release-keys" \
    BuildFingerprint=lge/timelm/timelm:13/TKQ1.220829.002/2318902104908:user/release-keys

# Device Flags

# Maintainer name (use "_" for spaces, e.g., "rmp_22" → "rmp 22" in UI)
AXION_MAINTAINER := Kiyomi

# Processor name (use "_" for spaces)
AXION_PROCESSOR := Snapdragon_865_5G

# Define rear camera specs (multiple sensors supported)
AXION_CAMERA_REAR_INFO := 64,13

# Define front camera specs
AXION_CAMERA_FRONT_INFO := 10

# BLur
TARGET_ENABLE_BLUR := true

# AxionFX
TARGET_INCLUDE_AXFX := true

# Fix Video Playback
TARGET_NEEDS_VULKAN_MEDIA_FIX := true
