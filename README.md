# TokenCaseStudy

Android Case Study Project for Token Inc.

- The application has 2 main UIs; Customer and POS. 
- It simulates the POS and customer interactions with QR codes.
- You can create QR Code from POS UI, and simulate the reading of that generated QR code on customer UI.
- User needs to be logged in in order to simulate both Customer and POS (POS UI does not need any authentication but in order to simulate QR Reading it is a must)
- User's payment history is stored at Google Firestore.
- Using Google Firebase for both authentication and database (for payment history).
- The source code cannot be compiled and/or cannot run without files that include sensitive information (such as Google Firebase config file and OSY-QR API's secret codes).
