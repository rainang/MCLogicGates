# Change Log
All notable changes to this project will be documented in this file.
This project adheres to [Semantic Versioning](http://semver.org/).

## [Unreleased]

## [1.0.3] - 2016-01-30
### Fixed
- Ender Converter ignoring other Converters on signal validation
- Ender Converter not notifying other Ender Converters of change

## [1.0.2] - 2016-01-30
### Fixed
- Converter signal transmission/reception
- Converter particles

## [1.0.1] - 2016-01-30
### Added
- Server/Client proxy handling

## [1.0.0] - 2016-01-30
### Added
- Vertical Receiver Blocks

### Changed
- Moved to Minecraft 1.8.9
- BUFFER and NOT gate recipes to reduce overall slab cost
- Renamed Vertical Repeater to Vertical Transmitter
- Recipes for Vertical Transmitter

### Fixed
- Block dropping wrong item and meta on break
- XNOR Gate recipe

## [0.11.1] - 2016-01-28
### Fixed
- Registering item render on wrong side

## [0.11.0] - 2016-01-28
### Added
- Basic gate crafting items (BUFFER, NOT, AND, OR, XOR, NAND, NOR, XNOR)
- Input toggle for 1-2 input logic gates
- 3 input logic gates
- 5 input vertical repeater
- New textures

### Changed
- All recipes to use new crafting items
- Removed delay toggle (may create a diode of its own)
- Removed locking (may be brought back later)

## [0.10.0] - 2016-01-26
### Changed
- BlockRedirector to BlockDirector
	- renamed all BlockDirector associated files

### Fixed
- BlockVerticalRepeater not powering properly
- BlockVerticalRepeater not notifying neighbors properly
- BlockGate not transitioning to proper state on activated
- BlockDirector displaying particles while off

## [0.9.0] - 2016-01-25
### Added
- New Repeater types - all repeaters come in both redstone and ender variants
	- Inverter
		- inverts signal
		- has delay toggle
		- can be locked
	- Converter
		- converts signal
		- has delay toggle
		- can be locked
	- Vertical
		- sends signals vertically
		- can receive signals from all sides except output
		- right click to change orientation (up/down)
	- Redirector
		- redirects signal
		- right click to change orientation (left/right)
	- Gates
		- AND
		- OR
		- NAND
		- NOR
		- XOR
		- XNOR

### Changed
- Name to Logic Gates
- All repeaters to new variants
- All recipes
- Removed all Caster Blocks

## [0.8.0] - 2016-01-19
### Added
- RR Caster
- More Caster on place orientation options
	- if placed on other Diode's face, will try to connect input to output or vice versa
	- if sneaking, will connect input to hit Block's face
	- otherwise defaults to same as BlockRedstoneRepeater
- More Caster I/O configuration by block activation
	- right click input or output to invert
	- shift + right click input to move output opposite of input
	- shift + right click output to move input opposite of output

### Changed
- Default tick delay to 1 tick
- Caster block hardness

### Fixed
- Subtle mistake in textures

## [0.7.2] - 2016-01-19
### Fixed
- Repeaters not locking properly
- Caster delay to 1 tick

## [0.7.1] - 2016-01-13
### Fixed
- Disabled currently unused Diode recipe causing crash

## [0.7.0] - 2016-01-13
### Added
- Caster ER to RE recipe and vice versa

### Fixed
- Mod showing wrong version number
- Caster reorienting to default state when attempting to change input or output to invalid side
- Diode base texture

## [0.6.0] - 2016-01-12
### Added
- Caster eye recipe
- Caster particles when powered

### Changed
- Caster block assets

### Fixed
- Repeater signal transmission and reception 

## [0.5.0] - 2016-01-04
### Added
- Caster blocks
- Removed vanilla repeater from creative tabs

### Changed
- Renamed most assets file names
- Rewrote block state and model files

## [0.4.0] - 2016-01-02
### Added
- Replaced vanilla repeater recipe with RR Repeater

### Changed
- Updated information in mcmod.info file

### Fixed
- Recipes allowing crafting with stone variants
- RE recipe

## [0.3.3] - 2016-01-01
### Fixed
- Locked repeater block model

## [0.3.2] - 2016-01-01
### Fixed
- Repeater notification to always notify output side block

## [0.3.1] - 2016-01-01
### Fixed
- Repeater ignoring locked repeaters when checking for input
- Repeater not notifying neighbors of change

## [0.3.0] - 2015-12-31
### Fixed
- Signal getting blocked by other repeaters
- Repeater not locking

## [0.2.0] - 2015-12-31
### Changed
- Repeater neighbor notifications

## [0.1.0] - 2015-12-31
### Added
- Initial framework