How to use a custom skin URL?
=============================

For example you received this command from a site like https://minecraft-heads.com

**command:**
::
  /give @p skull 1 3 {display:{Name:""},SkullOwner:{Id:"98958836-4e85-4e5d-9c20-6ee46723f202",Properties:{textures:[{Value:"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmY3ZWI3NWU1NTQyY2M0OTM3YWFhZDViYjg2NTczOTNlYWYwMjY1MDA2ZWFjMWRjOTY2OTFmMzJlMTY0MzcifX19"}]}}}


Select the import part from your command which hides the actual URL base64encoded.

**encoded url:**
::
  eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmY3ZWI3NWU1NTQyY2M0OTM3YWFhZDViYjg2NTczOTNlYWYwMjY1MDA2ZWFjMWRjOTY2OTFmMzJlMTY0MzcifX19

Copy the encoded url and paste it into any base64 decoder. I have used https://www.base64decode.org/ in this example.

.. image:: ../_static/images/faq1.png


Select the url after the "url" key.

**decoded url:**
::
  http://textures.minecraft.net/texture/6f7eb75e5542cc4937aaad5bb8657393eaf0265006eac1dc96691f32e16437


Congratulations, you can use the url anywhere in the plugin.

**config.yml**
::
    ball:
      skin: http://textures.minecraft.net/texture/6f7eb75e5542cc4937aaad5bb8657393eaf0265006eac1dc96691f32e16437
      size: NORMAL
      hitbox-size: 3.0
      carry-able: false
      always-bounce: true
      rotating: true
      modifiers:
        horizontal-touch: 1.0
        vertical-touch: 1.0
        horizontal-kick: 1.5
        vertical-kick: 6.0
        horizontal-throw: 1.0
        vertical-throw: 1.0
        rolling-distance: 1.5
        gravity: 0.7

