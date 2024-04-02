# Dive Surf Enterprise Integration

This repository contains a solution for integrating multiple systems using Apache Camel, a Java-based integration framework, along with Apache ActiveMQ, an open-source message broker. The solution implements various Enterprise Application Integration patterns to facilitate communication between different components.

The integration scenario revolves around Dive-Surf Inc., a company specializing in surfboards and diving suits. The integrated systems enable customers to place orders for surfboards and diving suits seamlessly.

## Overview

The project focuses on integrating several applications to streamline processes and data flow. It includes the integration of systems responsible for order generation, order processing, billing, inventory management, and result tracking.
![Integration Patterns Diagram](https://github.com/naseem-shawarba/Dive_Surf_Enterprise_Integration/blob/main/Dive-Surf-Integration-Patterns-Visualization.jpg)

## Implemented Integration Patterns

The solution utilizes a combination of integration patterns to achieve seamless communication between systems:

- **Message Endpoint**
- **Channel Adapter**
- **Publish-Subscribe Channel**
- **Point-to-Point Channel**
- **Aggregator**
- **Content-Based Router**
- **Message Translator**
- **Content Enricher**

## Components

The integrated systems include:

1. **WebOrderSystem**: Generates order strings for incoming orders.
2. **CallCenterOrderSystem**: Generates text files containing new orders periodically.
3. **BillingSystem**: Evaluates customer credit standing and validates orders.
4. **InventorySystem**: Checks item availability in the inventory.
5. **ResultSystem**: Collects and processes orders, distinguishing between valid and invalid ones.

## Getting Started

To run the integration solution, follow these steps:

1. Install Apache Camel and ActiveMQ.
2. Clone this repository to your local machine.
3. Navigate to the project directory.
4. Build and run each application separately using their respective main methods.

