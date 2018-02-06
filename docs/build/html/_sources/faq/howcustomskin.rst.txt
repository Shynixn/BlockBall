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


Congratulations, you can use the url anywhere in the plugin. Just do not forget to remove the http:// prefix.


**config.yml**
::
    ############################

    # GUI-settings

    # Settings for the petblock-gui

    # settings-title: GUI title
    # settings-copy-skin: Should the skin of petblock-engine be applied when being selected?
    # settings-use-only-disable-pet-item: Removes the enable block from the GUI and uses the pet-item instead to spawn the pet.
    # settings-my-pet-position: Position in the GUI to show the currently selected pet
    # settings-my-pet-default-appearance:: Default skin of the petblock if the settings-copy-skin is disabled

    # gui enabled: Should the the item be shown in the GUI?
    # gui position: Position of the item in of the GUI
    # gui page: Page of the item in of the GUI (PAGES: MAIN, WARDROBE)
    # gui id: item id in the GUI
    # gui damage: item damage in the the GUI
    # gui skin: item skin in the GUI (Name of the player [Shynixn] or the skin URL [textures.minecraft.net/texture/797884d451dc7b7729de2076cd6c4912865ade70391d1ccec3e95fb39f8c5e1])
    # gui name: item name in the GUI
    # gui unbreakable: item unbreakable tag in the GUI
    # gui lore: item lore in the GUI (Multiple lines support)

    ############################

    gui:
      settings:
        title: 'PetBlocks'
        copy-skin: true
        use-only-disable-pet-item: false
        default-engine: 1
      items:
        my-pet:
          position: 4
        default-appearance:
          id: 2
          damage: 0
          skin: 'textures.minecraft.net/texture/dfab7daeb8f333c7886a70ef30caf4dec4a8cd10493f23802f1516bdd23fcd'
          unbreakable: false
        empty-slot:
          enabled: true
          id: 160
          damage: 15
          skin: 'none'
          name: 'none'
          unbreakable: false
          lore:
            - 'none'

