Change the block skin
=====================

Let's change the skin of the cannon item.

Skin from player name
~~~~~~~~~~~~~~~~~~~~~

**GUI - before**

.. image:: ../_static/images/cust2.jpg

**config.yml - before**
::
    gui:
      settings:
        title: 'PetBlocks'
        copy-skin: true
        use-only-disable-pet-item: false
        default-engine: 1
      items:
        cannon-pet:
          enabled: true
          position: 11
          page: 'MAIN'
          id: 397
          damage: 3
          skin: 'textures.minecraft.net/texture/996754d330435345aae3a9f063cfca42afb28b7c5c4bb9f294ed2527d961'
          name: '&2&lLaunch'
          unbreakable: false
          lore:
            - '&7Shoot your pet like a cannon.'

Set the id to 397 for Mob Head and set damage back to 3 for Mob Head (Human).
Then set skin to the name of the player. In this case 'Shynixn'

**config.yml - after**
::
    gui:
      settings:
        title: 'PetBlocks'
        copy-skin: true
        use-only-disable-pet-item: false
        default-engine: 1
      items:
        cannon-pet:
          enabled: true
          position: 11
          page: 'MAIN'
          id: 397
          damage: 3
          skin: 'Shynixn'
          name: '&2&lLaunch'
          unbreakable: false
          lore:
            - '&7Shoot your pet like a cannon.'

If the skin does **not get loaded** and a ordinary Alex or Steve is shown please check out this `solution. <../faq/playerheadnotshown.html>`_

**GUI - after**

.. image:: ../_static/images/cust4.jpg

Skin from skin URL
~~~~~~~~~~~~~~~~~~~~~

**GUI - before**

.. image:: ../_static/images/cust2.jpg

**config.yml - before**
::
    gui:
      settings:
        title: 'PetBlocks'
        copy-skin: true
        use-only-disable-pet-item: false
        default-engine: 1
      items:
        cannon-pet:
          enabled: true
          position: 11
          page: 'MAIN'
          id: 397
          damage: 3
          skin: 'textures.minecraft.net/texture/996754d330435345aae3a9f063cfca42afb28b7c5c4bb9f294ed2527d961'
          name: '&2&lLaunch'
          unbreakable: false
          lore:
            - '&7Shoot your pet like a cannon.'


Set the id to 397 for Mob Head and set damage back to 3 for Mob Head (Human).
Retrieve a skin from a site like `https://minecraft-heads.com <https://minecraft-heads.com>`_

I have used this Give-Code in this example:

**Give-Code**
::
    /give @p skull 1 3 {display:{Name:"Pikachu"},SkullOwner:{Id:"e70e2912-ccb5-4b71-a499-1f9d5442e742",Properties:{textures:[{Value:"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjhmNmY0ZGM1NDgxYTVmZWQ0NGZjZGU3ZDlmYjQ4M2ZlY2IzNTA3ZjZiYjkyNDA4Yjg0NmM3NDdhOWNmMGMwIn19fQ=="}]}}}

Use this `solution <../faq/howcustomskin.html>`_ to parse the skin URL from the gift Code.

**skin-URL**
::
   http://textures.minecraft.net/texture/f8f6f4dc5481a5fed44fcde7d9fb483fecb3507f6bb92408b846c747a9cf0c0


Do not forget to **remove the http:// prefix** before including it into the config.yml.

**config.yml - after**
::
    gui:
      settings:
        title: 'PetBlocks'
        copy-skin: true
        use-only-disable-pet-item: false
        default-engine: 1
      items:
        cannon-pet:
          enabled: true
          position: 11
          page: 'MAIN'
          id: 397
          damage: 3
          skin: 'textures.minecraft.net/texture/f8f6f4dc5481a5fed44fcde7d9fb483fecb3507f6bb92408b846c747a9cf0c0'
          name: '&2&lLaunch'
          unbreakable: false
          lore:
            - '&7Shoot your pet like a cannon.'


**GUI - after**

.. image:: ../_static/images/cust5.jpg