# Blackjack Game for Sharp BASIC

**Author:** AFPV 1984
**File:** blackjack.bas

## Overview

A complete implementation of Blackjack (21) for the Sharp pocket computer, featuring configurable game settings, card counting support, and comprehensive statistics tracking.

## Features

### Game Configuration
- **Multiple Decks:** Choose 1-4 decks (default: 4)
- **Starting Cash:** Configurable (default: $200)
- **Win Limit:** Set a target cash amount (default: 2× starting cash)
- **Betting Limits:** Configurable minimum (default: $5) and maximum (default: $500) bets
- **Sound:** Toggle beep on/off
- **Card Counting:** Optional display of running count

### Gameplay Options

#### Player Actions
- **H** - Hit (draw another card)
- **S / Enter** - Stand/Stick (keep current hand)
- **D** - Double Down (double bet, receive one card, then stand)
- **X** - Split (split pairs into two hands)

#### Information Commands
- **C** - Toggle card counting display on/off
- **F** - Display current funds
- **G** - Show game statistics
- **T** - Display total bets made
- **\*** - Show command list
- **E** - End game and show final statistics

### Card Counting System

The program implements a basic card counting system (Hi-Lo):
- **Low cards (2-6):** +1 to count
- **Neutral cards (7-9):** 0 (no change)
- **High cards (10, J, Q, K, A):** -1 to count

When card counting is enabled (press C), the current count is displayed along with cards remaining in the deck.

### Game Rules

- Dealer draws to 17
- Blackjack (21 with two cards) pays 3:2
- Regular wins pay 1:1
- Insurance not implemented
- Split aces receive only one additional card each
- Deck is reshuffled when 85% depleted

### Statistics Tracked

- **Hands played:** Total number of hands
- **Blackjacks:** Natural 21s dealt to player
- **Win/Loss/Draw:** Complete breakdown of hand results
- **Cash range:** Minimum and maximum cash reached during session
- **Streaks:** Longest winning and losing streaks
- **Average Win/Loss:** Per-hand profit/loss calculation

## Code Structure

### Key Subroutines

- **"A"** (lines 640-710) - Deck shuffling routine
- **"D"** (lines 720-990) - Game initialization and configuration
- **"H"** (lines 1000-1040) - Card value calculation and count update
- **"Q"** (lines 1240-1280) - Hit card logic
- **"T"** (lines 1310-1450) - Dealer drawing logic
- **"DD"** (lines 1460-1520) - Double down handling
- **"SX"** (lines 1670-2450) - Split hand logic
- **"GA"** (lines 2460-2500) - Display game statistics
- **"END"** (lines 2570-2590) - Game ending and final report

### Game Flow

1. **Setup:** Configure game parameters or use standard settings
2. **Shuffle:** Randomize deck(s)
3. **Betting:** Place bet within min/max limits
4. **Deal:** Player receives 2 cards, dealer receives 2 (one hidden)
5. **Player Turn:** Choose actions (hit, stand, double down, split)
6. **Dealer Turn:** Dealer draws to 17 or busts
7. **Resolution:** Determine winner, update cash and statistics
8. **Repeat:** Continue until funds depleted or win limit reached

## Technical Notes

- Uses machine code routine (lines 140-210) for sound/visual effects during title display
- Includes a separate program renumbering utility (lines 50-110)
- Implements efficient deck representation using string arrays
- Cards are dealt sequentially from shuffled deck, with reshuffle at 85% depletion
- Supports multiple simultaneous hands when splitting pairs
- Tracks both soft and hard totals for ace handling

## Game Over Conditions

1. **Insufficient Funds:** Cash falls below minimum bet
2. **Win Limit Reached:** Cash exceeds configured win limit
3. **Manual Exit:** Player presses 'E' to end game

Upon game over, the program displays:
- Final cash amount and profit/loss
- Complete game statistics
- Total amount wagered
- Average win/loss per hand
