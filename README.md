# Torrent Tools

A modern command-line BitTorrent tool for inspecting, creating, and modifying torrent metafiles.

## Features

- **Info**: Display detailed information about torrent files
- **Create**: Generate new torrent files from files or directories
- **Edit**: Modify existing torrent files
- **Magnet**: Generate magnet URIs from torrent files

## Requirements

- JDK 17 or higher
- Maven 3.6+
- (Optional) GraalVM for native image compilation

## Build

### Build JAR

```bash
mvn clean package
```

This creates:
- `target/torrent-tools-1.0.0.jar` - Main JAR
- `target/torrent-tools-1.0.0-jar-with-dependencies.jar` - Fat JAR with all dependencies

### Build Native Executable (Optional)

Requires GraalVM with native-image support:

```bash
mvn clean package -Pnative
```

This creates a native executable at `target/torrent` (Linux/macOS) or `target/torrent.exe` (Windows).

## Usage

### Using Fat JAR

```bash
java -jar target/torrent-tools-1.0.0-jar-with-dependencies.jar --help
```

### Using Native Executable

```bash
./target/torrent --help
```

### Commands

#### Help

```bash
torrent --help
```

#### View Torrent Info

```bash
# Basic info
torrent info example.torrent

# Raw JSON output
torrent info --raw example.torrent
```

#### Create Torrent

```bash
# Create from a file
torrent create /path/to/file

# Create from a directory
torrent create /path/to/directory

# With custom announce URL
torrent create -a "https://tracker.example.com/announce" /path/to/file

# With custom options
torrent create \
  -o output.torrent \
  -a "https://tracker1.com/announce,https://tracker2.com/announce" \
  -c "My comment" \
  -n "Custom Name" \
  -l 256K \
  -p \
  /path/to/file
```

**Create Options:**
- `-o, --output`: Output file path (default: `<name>.torrent`)
- `-a, --announce`: Announce URL(s), comma-separated
- `-c, --comment`: Torrent comment
- `-n, --name`: Custom torrent name
- `-l, --piece-size`: Piece size (e.g., `256K`, `1M`, `auto`)
- `-p, --private`: Set private flag
- `-s, --source`: Source identifier
- `--created-by`: Creator name
- `--publisher`: Publisher name
- `-d, --creation-date`: Creation date (ISO-8601 or POSIX time)
- `--no-created-by`: Omit creator field
- `--no-creation-date`: Omit creation date
- `--no-publisher`: Omit publisher field
- `--no-source`: Omit source field

#### Edit Torrent

```bash
# Edit announce URL
torrent edit -a "https://new-tracker.com/announce" example.torrent

# Edit multiple fields
torrent edit \
  -c "New comment" \
  -n "New Name" \
  -p yes \
  -o edited.torrent \
  example.torrent

# Remove fields
torrent edit \
  --no-announce \
  --no-creation-date \
  example.torrent
```

**Edit Options:**
- `-o, --output`: Output file path
- `-a, --announce`: New announce URL(s)
- `-c, --comment`: New comment
- `-n, --name`: New torrent name
- `-p, --private`: Set private flag (`yes`/`no`)
- `-s, --source`: New source
- `--create-by`: New creator name
- `--publisher`: New publisher
- `-d, --creation-date`: New creation date
- `--no-announce`: Remove announce information
- `--no-created-by`: Remove creator field
- `--no-creation-date`: Remove creation date
- `--no-publisher`: Remove publisher field
- `--no-source`: Remove source field

#### Generate Magnet URI

```bash
torrent magnet example.torrent
```

## Project Structure

```
src/
├── main/
│   └── java/
│       └── com/github/anicmv/torrenttools/
│           ├── TorrentToolsApplication.java  # Main entry point
│           ├── command/                       # CLI commands
│           │   ├── InfoCommand.java
│           │   ├── CreateCommand.java
│           │   ├── EditCommand.java
│           │   └── MagnetCommand.java
│           ├── codec/                         # Bencode encoding/decoding
│           │   ├── BEncodeCodec.java
│           │   ├── BEncodeInputStream.java
│           │   └── BEncodeOutputStream.java
│           ├── meta/                          # Data models
│           │   └── TorrentInfo.java
│           ├── service/                       # Business logic
│           │   ├── TorrentService.java
│           │   ├── TorrentCreatorService.java
│           │   └── TorrentEditorService.java
│           └── util/                          # Utilities
│               └── TreePrinter.java
└── test/
    └── java/
        └── com/github/anicmv/torrenttools/
            └── codec/
                └── BEncodeCodecTest.java
```

## Development

### Run with Maven

```bash
# Run with arguments
mvn exec:java -Dexec.mainClass="com.github.anicmv.torrenttools.TorrentToolsApplication" -Dexec.args="info example.torrent"
```

### Run Tests

```bash
mvn test
```

## License

MIT License

## Author

Developed with ❤️ by anicmv
