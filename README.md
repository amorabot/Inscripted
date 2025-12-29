![logoextended2](https://github.com/user-attachments/assets/429ff53e-89d1-4c67-a5ec-6bd421ae5789)

Hey there, I'm Daniel (or Amora)! Inscripted is a Minecraft Java Plug-in that aims to implement Action-RPG elements into the game. I started this project mainly as way to learn and deeply understand the Java language and game programming in general (with the challenges of coding for a game server)!


Taking inspiration from other RPGs and custom Minecraft RPG servers I've played before, I wanted to see if I could make a game of my own inside Minecraft and face (and overcome) some of the design and technical challenges that come with it!


I mainly wanted to create a item system that felt alive and dynamic, taking massive inspiration from games like Path of Exile, and a combat system that enables a diverse Player vs. Player experience and where every class (or Archetype, in Inscripted's case) has a clear purpose (inspired by Tibia in some aspects), while taking a "You-are-what-you-wear" approach to character customization.

Down below there is showcase of the project's current state, covering its design and visuals. Feel free to dowload the pre-built .jar for the latest GitHub release or check out the copious amounts of jank in the repo and build it yourself! I'd love to hear any feedback!

### If you want to contact me, you can do so via:
  - Discord: <b>@amorabot</b>
  - E-mail: amorabotdev@gmail.com

-----------------------------------------------------------------------------------------------------------------

## Gameplay Demos:
- v1: https://www.youtube.com/watch?v=7vwqPahws5U
- v3 (1): https://www.youtube.com/watch?v=3HJHwcDx894
- v3 (2): https://www.youtube.com/watch?v=OdptyzORqxc

![2024-05-07_21 52 34](https://github.com/amorabot/Inscripted/assets/16783145/a5820915-be36-45b0-ac10-8bf4d91aedbd)

<details>
  <summary>More of the playable worlds!</summary>

  ![thumb4](https://github.com/amorabot/Inscripted/assets/16783145/8d6b0652-22f4-40e6-9aca-0090488b4a60)

  
</details>


![ItemSystem](https://github.com/amorabot/Inscripted/assets/16783145/27e9172e-762a-4f3b-98c1-a1524b7c4bc1)

### The item system is meant to allow for expressive build creation, mixing and matching all the different items and their stats to fit your preferred style!

Equippable items can be one of two types: **Craftables** or **Relics**! Craftable items can have their Inscriptions modified with currencies such as Orbs, while Relic items cannot be modified.

<img width="350" height="465" alt="approaching_winter" src="https://github.com/user-attachments/assets/b62fd521-ca83-4baf-8464-e273df5e4175" />
<img width="428" height="435" alt="sword" src="https://github.com/user-attachments/assets/7e1bbb84-c5d0-4e09-adac-f0555f8d84bd" />


A Relics main purpose is to turn some of the game mechanics on their head and subvert the use of some stats, allowing for even quirkier builds!
This way you can, for instance, take advantage of the Inscription combinations available only for certain item [Archetypes](#The-Attribute-trinity), while counter-balancing the negative effects of a mighty Relic!

### One of the core principles of the item system is that obtaining a desired item is pretty much a deterministic process.
### Not easy. Deterministic.

Though item crafting and Relics, you can most certainly find a combination that fits your playstyle!

The itemization was designed around the concepts of item Archetypes and attribute-alignment. There are no strict class systems, only items. You can wear any item from any Archetype at any time, the choice is yours!

## The following section's purpose will be to explain these core concepts of the item system:

-----------------------------------------------------------------------------------------------------------------

![Archetypes](https://github.com/amorabot/Inscripted/assets/16783145/eaa49118-0b6f-42bf-abdd-87c69b76f8cf)

## Pure Archetypes & The Attribute trinity:

- Archetypes are centered around the <b>Attribute trinity</b>, Strength (STR), Dexterity (DEX) and Intelligence (INT).
- Pure Strength is represented by the <b>Marauder</b> Archetype, that itself represents a "Slow Brute" in terms of item stats. Being Slow, Highly defensive and physically overpowering.
- Pure Dexterity is represented by the <b>Mercenary</b> Archetype, that itself represents a "Agile Trickster" in terms of item stats. Being Highly mobile, precise and evasive.
- Pure Intelligence is represente by the <b>Sorcerer</b> Archetype, that itself represents a "Versitile elementalist" in terms of item stats. Having crowd control and explosive elemental prowess.
- Each Attribute on the trinity is also typically aligned with a in-game element. Example: STR items are typically FIRE themed, Sorcerer items will have more ICE modifiers.
- There are <b>3 more Hybrid Archetypes</b> that "mix" themes and elements fromm their "parent" Archetypes. They're generally more complex but more versitile versions of the pure Archetypes, being generally harder to master or itemize.

<img width="1024" height="640" alt="pure" src="https://github.com/user-attachments/assets/516beedb-4108-43c9-a50d-8ae04d823731" />

## Hybrid Archetypes

As stated previously, <b>Hybrid Archetypes</b> inherit characteristics from both of their "Parent Archetypes" and are more complex versions of them, mixing all their characteristics in a single item. That can be chaotic and potentially very powerful!
  - Lets say you want a "Agile Brute", You can simply combine Marauder and Mercenary items individually! But that can leave you spreading too thin for item stats or having no quick-paced melee options. The Gladiator fits all of that in its own Archetype! That way you can dictate how "brute" or how "agile" you want your Gladiator to be or, for instance, how "Gladiator" you want your Marauder to be

<img width="1024" height="640" alt="fusion" src="https://github.com/user-attachments/assets/5f999bed-6c18-4647-b672-6b0937d8cad1" />


-----------------------------------------------------------------------------------------------------------------

## Archetype Diagram

Archetypes can be of two types: <b>Pure</b> and <b>Hybrid</b>. Pure Archetypes will typically be best when trying to specialize in a particular stat or set of attribute-aligned stats. Hybrid Archetypes are more chaotic but can serve as a good way to have a more diverse build. When tailored and mixed together correctly, they can be greater than the sum of its parts!
- Marauder is the Pure STR Archetype
- Gladiator is the Hybrid Archetype that mixes STR and DEX characteristics
- Mercenary is the Pure DEX Archetype
- Rogue is the Hybrid Archetype that mixes INT and DEX characteristics
- Sorcerer is the Pure INT Archetype
- Templar is the Hybrid Archetype that mixes STR and INT characteristics

<img width="1024" height="640" alt="triad" src="https://github.com/user-attachments/assets/9b2e47e4-840e-4803-8fd8-262655cfff25" />


-----------------------------------------------------------------------------------------------------------------

![Weapons](https://github.com/amorabot/Inscripted/assets/16783145/b6835343-d477-480b-9882-df5a667b71bd)


<details>

<summary>Click here to see some weapon examples!</summary>

<img width="456" height="390" alt="bow" src="https://github.com/user-attachments/assets/824ccc56-d0db-4202-9ab4-791bc4d65ccc" />

<img width="462" height="333" alt="axe_aug" src="https://github.com/user-attachments/assets/f1174b8a-8672-4881-84c5-702f4238e5d8" />

<img width="428" height="435" alt="sword" src="https://github.com/user-attachments/assets/8d1eed81-6938-4402-acc4-4c691ceda750" />

</details>


![Armors](https://github.com/amorabot/Inscripted/assets/16783145/d245a6d7-d985-4db4-965d-50d9c834dd5d)


<details>

<summary>Click here to see some armor examples!</summary>

<img width="487" height="357" alt="glad" src="https://github.com/user-attachments/assets/7df68d5c-0045-436b-a4e3-d57ad0cab0d7" />

<img width="490" height="508" alt="incan_heart" src="https://github.com/user-attachments/assets/5eb86b24-29e7-4b4c-8a98-ff2ffad7fa0d" />

</details>

# Core Archetypes: 

-----------------------------------------------------------------------------------------------------------------

![marauderLogoColored](https://github.com/amorabot/Inscripted/assets/16783145/847efa3d-9c72-40db-867d-26facb9f0eb7)
![STR](https://github.com/amorabot/Inscripted/assets/16783145/d3ac961a-b556-4d9e-b672-b25967463820)


<details>
  
The Marauder is a warrior that wears heavy and protective armor and specilizes in strong physical blows, shredding through the enemies defences.

Their weapon of choice is the Axe, wearing Heavy Plating armor.

![Shield](https://github.com/amorabot/Inscripted/assets/16783145/000e65c4-7794-4120-8662-b1741e6dbd31)
*Defences:*


Heavy phisical mitigation.

![Sword](https://github.com/amorabot/Inscripted/assets/16783145/f1f7c4bd-9f27-4827-837b-2eb10d271a86)
*Offences:*

Physical Damage and penetration.

</details>

<br/><br/>
-----------------------------------------------------------------------------------------------------------------

![gladLogoColored](https://github.com/amorabot/Inscripted/assets/16783145/2b2bdce4-4e39-4928-a77b-ed826d4f0fd1)
![STR](https://github.com/amorabot/Inscripted/assets/16783145/d3ac961a-b556-4d9e-b672-b25967463820)
![DEX](https://github.com/amorabot/Inscripted/assets/16783145/c67f9dcd-c9c3-4956-b1fa-3e7e78c6be5c)

<details>

A fierce duelist that excels at battle. the Gladiator prefers precise and quicker blows to finish their enemies. And of course, all of that
while wearing the fanciest of armor, showing off their agility and form in combat.

Their weapon of choice is the Sword, wearing Carved Plating armor.

![Shield](https://github.com/amorabot/Inscripted/assets/16783145/000e65c4-7794-4120-8662-b1741e6dbd31)
*Defences:*


Moderate physical mitigation and evasion.


![Sword](https://github.com/amorabot/Inscripted/assets/16783145/f1f7c4bd-9f27-4827-837b-2eb10d271a86)
*Offences:*


Damage variety, Stamina and Critical strikes.
  
</details>

<br/><br/>
-----------------------------------------------------------------------------------------------------------------

![mercLogoColored](https://github.com/amorabot/Inscripted/assets/16783145/8b406f3f-c479-477c-a7f0-0d6f3b5875cb)
![DEX](https://github.com/amorabot/Inscripted/assets/16783145/c67f9dcd-c9c3-4956-b1fa-3e7e78c6be5c)

<details>

The Mercenary is agile and decisive, having long range and high mobility to dictate the pace of battle.

Their weapon of choice is the Bow/Crossbow, wearing Light Cloth armor.

![Shield](https://github.com/amorabot/Inscripted/assets/16783145/000e65c4-7794-4120-8662-b1741e6dbd31)
*Defences:*


Highly evasive and mobile.


![Sword](https://github.com/amorabot/Inscripted/assets/16783145/f1f7c4bd-9f27-4827-837b-2eb10d271a86)
*Offences:*

Long-range physical damage, penetration and ailments.
  
</details>

<br/><br/>
-----------------------------------------------------------------------------------------------------------------

![rogueLogoColored](https://github.com/amorabot/Inscripted/assets/16783145/91577227-b5e5-436d-bf74-a3c46ee5b289)
![DEX](https://github.com/amorabot/Inscripted/assets/16783145/c67f9dcd-c9c3-4956-b1fa-3e7e78c6be5c)
![INT](https://github.com/amorabot/Inscripted/assets/16783145/85a0fec7-05a0-4065-9d50-01beb940a423)

<details>

Although The Rogue has shorter range than average, they're elusive and sneaky. They can vanish before your eyes and strike a deadly backstab in a instant!

Their weapon of choice is the Dagger, wearing Runic Leather armor.

![Shield](https://github.com/amorabot/Inscripted/assets/16783145/000e65c4-7794-4120-8662-b1741e6dbd31)
*Defences:*


Moderate evasion and ward.


![Sword](https://github.com/amorabot/Inscripted/assets/16783145/f1f7c4bd-9f27-4827-837b-2eb10d271a86)
*Offences:*

Backstab damage multiplier, critical strikes and multipliers.
  
</details>

<br/><br/>
-----------------------------------------------------------------------------------------------------------------

![sorcLogoColored](https://github.com/amorabot/Inscripted/assets/16783145/d9fcf757-806c-4386-8c70-571f91ed0ac6)
![INT](https://github.com/amorabot/Inscripted/assets/16783145/85a0fec7-05a0-4065-9d50-01beb940a423)

<details>

The Sorcerer conjures the arcane in combat to cast powerful projectiles and spells. Although not as mobile as the Mercenary, they're very adaptable and a force to be reckoned with.

Their weapon of choice is the Wand, wearing Enchanted Silk armor.

![Shield](https://github.com/amorabot/Inscripted/assets/16783145/000e65c4-7794-4120-8662-b1741e6dbd31)
*Defences:*


Massive ward pool.


![Sword](https://github.com/amorabot/Inscripted/assets/16783145/f1f7c4bd-9f27-4827-837b-2eb10d271a86)
*Offences:*

Elemental damage and penetration.
  
</details>

<br/><br/>
-----------------------------------------------------------------------------------------------------------------

![templarLogoColored](https://github.com/amorabot/Inscripted/assets/16783145/623f998c-b214-48bb-bbe9-c51f53b2a322)
![INT](https://github.com/amorabot/Inscripted/assets/16783145/85a0fec7-05a0-4065-9d50-01beb940a423)
![STR](https://github.com/amorabot/Inscripted/assets/16783145/d3ac961a-b556-4d9e-b672-b25967463820)

<details>

The Templar is a righteous warrior that has the might and the faith to overcome their foes. They specialize in area effects and buffs with their prayers, being a bastion for those in need.

Their weapon of choice is the Mace, wearing Runic Steel armor.

![Shield](https://github.com/amorabot/Inscripted/assets/16783145/000e65c4-7794-4120-8662-b1741e6dbd31)
*Defences:*


Moderate physical mitigation and ward.


![Sword](https://github.com/amorabot/Inscripted/assets/16783145/f1f7c4bd-9f27-4827-837b-2eb10d271a86)
*Offences:*

Heavy elemental damage, area damage.
  
</details>

-----------------------------------------------------------------------------------------------------------------
