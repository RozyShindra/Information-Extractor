

# Information Extraction API

[![Java](https://img.shields.io/badge/Java-17+-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.x-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Maven](https://img.shields.io/badge/Maven-3.6+-blue.svg)](https://maven.apache.org/)
![Stanford CoreNLP](https://img.shields.io/badge/Stanford_CoreNLP-4.5.10-red.svg)

A **Java + Spring Boot REST API** for **Named Entity Recognition (NER)** and **Information Extraction** using [Stanford CoreNLP](https://stanfordnlp.github.io/CoreNLP/).
The service exposes endpoints to extract entities such as **Person, City, Country, State, Email, Title**, and more from raw text.

---

## 🚀 Features

* Tokenization, Sentence Splitting, POS Tagging, Lemmatization
* Named Entity Recognition (NER)
* Easy REST API interface (`/information-extractor/ner`)
* Built with **Spring Boot** + **Stanford CoreNLP**
  
---

## ⚙️ Installation & Setup

### 1. Clone the repository

```bash
git clone https://github.com/RozyShindra/Information-Extractor.git
cd Information-Extractor
```

### 2. Build the project

Make sure you have **Maven** installed and added to PATH.

```bash
mvn clean install
```

### 3. Run the Spring Boot application

```bash
mvn spring-boot:run
```

The service will start at:
`http://localhost:8080/information-extractor/ner`

---
<img width="1915" height="970" alt="image" src="https://github.com/user-attachments/assets/caa214ae-2e71-4c5b-9d04-4e581af0db24" />
---

## 📖 Future Enhancements

* Add **Sentiment Analysis API**
* Extend to support **custom entity extraction**
* Dockerize for deployment
* Integrate with frontend client
