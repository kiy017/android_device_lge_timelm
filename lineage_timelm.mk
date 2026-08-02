#
# SPDX-FileCopyrightText: The LineageOS Project
# SPDX-License-Identifier: Apache-2.0
#

# Inherit from those products. Most specific first.
$(call inherit-product, $(SRC_TARGET_DIR)/product/core_64_bit.mk)
$(call inherit-product, $(SRC_TARGET_DIR)/product/full_base_telephony.mk)

# Inherit from device
$(call inherit-product, device/lge/timelm/device.mk)

# Inherit some common Lineage stuff.
$(call inherit-product, vendor/lineage/config/common_full_phone.mk)

# Device identifier. This must come after all inclusions.
PRODUCT_NAME := lineage_timelm
PRODUCT_DEVICE := timelm
PRODUCT_MANUFACTURER := LGE
PRODUCT_BRAND := lge
PRODUCT_MODEL := LM-V600

# Lunaris
TARGET_CUSTOM_UDFPS := true
WITH_GMS := true
WITH_PIXEL_LAUNCHER := false
TARGET_USE_MAPS := true
TARGET_USE_FILES := true

PRODUCT_GMS_CLIENTID_BASE := android-lge

PRODUCT_BUILD_PROP_OVERRIDES += \
    DeviceName=timelm \
    BuildDesc="timelm-user 13 TKQ1.220829.002 2318902104908 release-keys" \
    BuildFingerprint=lge/timelm/timelm:13/TKQ1.220829.002/2318902104908:user/release-keys
