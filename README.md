<p align="center">
    <img src="https://github.com/ClementVicart/PixelCount/blob/main/kmp/assets/icon-192.png?raw=true" alt="PixelCount Logo"/>
</p>

# PixelCount

PixelCount is a Kotlin Multiplatform application designed to help you track and split expenses between groups of friends or colleagues. Whether you're on a ski vacation or sharing a dinner, PixelCount makes it easy to manage who owes what.

## ✨ Features

- **Expense Groups**: Create dedicated groups for different events or trips.
- **Participants**: Add multiple participants to each group.
- **Expense Tracking**: Log various types of transactions:
    - **Payments**: Standard expenses paid by one person for others.
    - **Refunds**: Track when someone is paid back.
    - **Transfers**: Log money moving between participants.
- **Balance Calculation**: Automatically calculate balances to see who owes whom and how much.
- **Emoji Customization**: Personalize your expense groups with a built-in emoji picker.
- **Multi-language Support**: Available in English and French.

## 📷 Screenshots

<p align="center">
  <img src="https://github.com/ClementVicart/PixelCount/blob/main/docs/Screenshot_20260123_221927.png?raw=true" width="150"/>
  <img src="https://github.com/ClementVicart/PixelCount/blob/main/docs/Screenshot_20260123_221936.png?raw=true" width="150"/>
  <img src="https://github.com/ClementVicart/PixelCount/blob/main/docs/Screenshot_20260123_221942.png?raw=true" width="150"/>
  <img src="https://github.com/ClementVicart/PixelCount/blob/main/docs/Screenshot_20260123_221947.png?raw=true" width="150"/>
</p>

## 🚀 Platforms

PixelCount is built using **Kotlin Multiplatform** and **Compose Multiplatform**, currently supporting:
- **Android**
- **Desktop** (Windows, Linux)

## 🛠 Technology Stack

- **UI Framework**: [Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/)
- **Database**: [SQLDelight](https://cashapp.github.io/sqldelight/) for local data persistence.
- **Serialization**: [Kotlinx Serialization](https://github.com/Kotlin/kotlinx.serialization)
- **DateTime**: [Kotlinx DateTime](https://github.com/Kotlin/kotlinx-datetime)

