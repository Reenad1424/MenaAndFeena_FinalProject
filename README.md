# 🏡🌳 Mena & Feena | منا وفينا

## Project Description | وصف المشروع

### Arabic

منا وفينا هي منصة مجتمعية ذكية تهدف إلى تعزيز الترابط والتكافل بين سكان الأحياء من خلال توفير خدمات رقمية متكاملة، مثل الإبلاغ عن المشكلات، المشاركة في الفعاليات والمبادرات، التواصل مع الجيران، انتخاب عمدة الحي، واستعراض المعالم القريبة.

كما توفر المنصة سوقًا مجتمعيًا لبيع وتأجير وتبادل المنتجات بين السكان، مع إدارة الطلبات والمدفوعات والتأمينات. وتعتمد على تقنيات الذكاء الاصطناعي وتحليل البيانات لتحسين جودة الحياة ودعم اتخاذ القرار وتعزيز المشاركة المجتمعية داخل الأحياء.

### English

Mena & Feena is a smart community platform designed to strengthen social connections and cooperation among neighborhood residents through integrated digital services such as issue reporting, community events and initiatives, neighbor communication, neighborhood mayor elections, and nearby landmark discovery.

The platform also provides a community marketplace for buying, renting, and exchanging products, including order management, payments, and security deposits. By leveraging Artificial Intelligence and data analytics, Mena & Feena aims to improve quality of life, support decision-making, and increase community engagement within neighborhoods.

---

## 📊 System Diagrams

### Class Diagram
![Class Diagram](https://github.com/Reenad1424/MenaAndFeena_FinalProject/blob/main/classDiagram.png)

### Use Case Diagram

![Class Diagram](https://github.com/Reenad1424/MenaAndFeena_FinalProject/blob/main/image.png)


## 🚀 Deployment

http://menaandfeena-env.eba-dsbymcfp.eu-central-1.elasticbeanstalk.com/api/v1/users/welcome

## 📮 API Documentation

https://github.com/Reenad1424/MenaAndFeena_FinalProject/blob/main/API%20documentation.postman_collection%20(1).json

# 🎨 Figma Design:

https://www.figma.com/design/e3KckUskVxFWFs5o5K72JD/%D9%85%D9%86%D9%8B%D8%A7-%D9%88%D9%81%D9%8A%D9%86%D8%A7?node-id=0-1&t=orEb4zsaxuzTMBDy-1


## 🛠 Technologies Used

* Java
* Spring Boot
* Spring Security
* JWT Authentication
* Spring Data JPA
* MySQL
* OpenAI API
* WhatsApp API
* Email Service
* OpenStreetMap API
* Overpass API
* PDF Generation
* Postman

---

## 📦 Modules Implemented

### Models

* User
* Neighborhood
* FamilyMember
* Landmark
* ElectionRound
* MayorCandidate
* MayorProfile
* MayorVote

### Security

* JwtService
* JwtAuthenticationFilter

---

## 🔐 Authentication

### Auth Controller

| Feature  | Method | Endpoint                |
| -------- | ------ | ----------------------- |
| Register | POST   | `/api/v1/auth/register` |
| Login    | POST   | `/api/v1/auth/login`    |

---

## 🗳 Election Round Controller

| Feature                    | Method | Endpoint                                        |
| -------------------------- | ------ | ----------------------------------------------- |
| Get All Election Rounds    | GET    | `/api/v1/election-rounds/get-all`               |
| Get Election Round Details | GET    | `/api/v1/election-rounds/get/{roundId}/details` |
| Create Election Round      | POST   | `/api/v1/election-rounds/add`                   |
| Update Election Round      | PUT    | `/api/v1/election-rounds/update/{roundId}`      |
| Delete Election Round      | DELETE | `/api/v1/election-rounds/delete/{roundId}`      |

---

## 👨‍👩‍👧 Family Members Controller

| Feature                | Method | Endpoint                                         |
| ---------------------- | ------ | ------------------------------------------------ |
| Get All Family Members | GET    | `/api/v1/family-members/get-all`                 |
| Add Family Member      | POST   | `/api/v1/family-members/add`                     |
| Update Family Member   | PUT    | `/api/v1/family-members/update/{familyMemberId}` |
| Delete Family Member   | DELETE | `/api/v1/family-members/delete/{familyMemberId}` |

---

## 📍 Landmark Controller

| Feature            | Method | Endpoint                                |
| ------------------ | ------ | --------------------------------------- |
| Get All Landmarks  | GET    | `/api/v1/landmarks/get-all`             |
| Add Landmark       | POST   | `/api/v1/landmarks/add`                 |
| Update Landmark    | PUT    | `/api/v1/landmarks/update/{landmarkId}` |
| Delete Landmark    | DELETE | `/api/v1/landmarks/delete/{landmarkId}` |
| Sync Landmarks     | POST   | `/api/v1/landmarks/sync`                |
| Nearby Landmarks   | GET    | `/api/v1/landmarks/nearby`              |
| Landmark Dashboard | GET    | `/api/v1/landmarks/dashboard`           |

---

## 👑 Mayor Candidate Controller

| Feature                 | Method | Endpoint                                             |
| ----------------------- | ------ | ---------------------------------------------------- |
| Get All Candidates      | GET    | `/api/v1/mayor-candidates/get-all`                   |
| Apply For Candidacy     | POST   | `/api/v1/mayor-candidates/apply/round/{roundId}`     |
| Get Candidates By Round | GET    | `/api/v1/mayor-candidates/round/{roundId}`           |
| Election Dashboard      | GET    | `/api/v1/mayor-candidates/round/{roundId}/dashboard` |
| Candidate Profile       | GET    | `/api/v1/mayor-candidates/profile/{candidateId}`     |
| Update Candidate        | PUT    | `/api/v1/mayor-candidates/update/{candidateId}`      |
| Delete Candidate        | DELETE | `/api/v1/mayor-candidates/delete/{candidateId}`      |

---

## 🏛 Mayor Profile Controller

| Feature                | Method | Endpoint                             |
| ---------------------- | ------ | ------------------------------------ |
| Get All Mayor Profiles | GET    | `/api/v1/mayor-profile/get-all`      |
| Add Mayor Profile      | POST   | `/api/v1/mayor-profile/add`          |
| Update Mayor Profile   | PUT    | `/api/v1/mayor-profile/update/{id}`  |
| Delete Mayor Profile   | DELETE | `/api/v1/mayor-profile/delete/{id}`  |
| Analytics              | GET    | `/api/v1/mayor-profile/analytics`    |
| Reports                | GET    | `/api/v1/mayor-profile/reports`      |
| Weekly Report          | GET    | `/api/v1/mayor-profile/weekly`       |
| Performance Report     | GET    | `/api/v1/mayor-profile/performance`  |
| Satisfaction Report    | GET    | `/api/v1/mayor-profile/satisfaction` |

---

## 🗳 Mayor Vote Controller

| Feature            | Method | Endpoint                                                           |
| ------------------ | ------ | ------------------------------------------------------------------ |
| Get All Votes      | GET    | `/api/v1/mayor-votes/get-all`                                      |
| Vote For Candidate | POST   | `/api/v1/mayor-votes/vote/candidate/{candidateId}/round/{roundId}` |
| Delete Vote        | DELETE | `/api/v1/mayor-votes/delete/{voteId}`                              |

---

## 🏘 Neighborhood Controller

| Feature                | Method | Endpoint                                        |
| ---------------------- | ------ | ----------------------------------------------- |
| Get All Neighborhoods  | GET    | `/api/v1/neighborhoods/get-all`                 |
| Add Neighborhood       | POST   | `/api/v1/neighborhoods/add`                     |
| Update Neighborhood    | PUT    | `/api/v1/neighborhoods/update/{neighborhoodId}` |
| Delete Neighborhood    | DELETE | `/api/v1/neighborhoods/delete/{neighborhoodId}` |
| Neighborhood Dashboard | GET    | `/api/v1/neighborhoods/dashboard`               |

---

## 👤 User Controller

| Feature                | Method | Endpoint                                   |
| ---------------------- | ------ | ------------------------------------------ |
| Get All Users          | GET    | `/api/v1/users/get-all`                    |
| Add User               | POST   | `/api/v1/users/add`                        |
| Update User            | PUT    | `/api/v1/users/update/{userId}`            |
| Delete User            | DELETE | `/api/v1/users/delete/{userId}`            |
| Welcome Page           | GET    | `/api/v1/users/welcome`                    |
| About Page             | GET    | `/api/v1/users/about`                      |
| Contact                | POST   | `/api/v1/users/contact`                    |
| Neighborhood Residents | GET    | `/api/v1/users/neighborhood-residents`     |
| Activity Log           | GET    | `/api/v1/users/activity-log`               |
| Full Profile           | GET    | `/api/v1/users/profile/full`               |
| Basic Profile          | GET    | `/api/v1/users/profile/basic`              |
| Family Profile         | GET    | `/api/v1/users/profile/family`             |
| Votes Profile          | GET    | `/api/v1/users/profile/votes`              |
| Events Profile         | GET    | `/api/v1/users/profile/events`             |
| Reviews Profile        | GET    | `/api/v1/users/profile/reviews`            |
| Issues Profile         | GET    | `/api/v1/users/profile/issues`             |
| Marketplace Summary    | GET    | `/api/v1/users/marketplace/summary`        |
| My Orders              | GET    | `/api/v1/users/marketplace/my-orders`      |
| Product Orders         | GET    | `/api/v1/users/marketplace/product-orders` |

---

## 👨‍💻 Team Members

* Reenad Almadhi
* Walaa Alrashidi
* Abdullah Alrasheed

---

## 🎓 Project

**Tuwaiq Academy – Java Web Development Bootcamp Final Project**
