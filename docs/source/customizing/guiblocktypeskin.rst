Change the block type and damage
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Let's change the block type of the cannon item.

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

The items in the gui.items section of the config.yml are called the static items. The names like "cannon-pet" cannot be
changed and new items **cannot** be added.

Change the id to 332 for snowball and set damage back to 0. Also, it is recommend to replace the skin with 'none' if the id
is not 397 and damage is not 3.

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
          id: 332
          damage: 0
          skin: 'none'
          name: '&2&lLaunch'
          unbreakable: false
          lore:
            - '&7Shoot your pet like a cannon.'


**GUI - after**

.. image:: ../_static/images/cust3.jpg



