🏡🌳 Mena & Feena | منا وفينا
Project Description | وصف المشروع
Arabic
منا وفينا هي منصة مجتمعية ذكية تهدف إلى تعزيز الترابط والتكافل بين سكان الأحياء من خلال توفير خدمات رقمية متكاملة. تُمكّن السكان من الإبلاغ عن المشكلات، المشاركة في الفعاليات والمبادرات المجتمعية، التواصل مع الجيران، انتخاب عمدة الحي، واستعراض المعالم القريبة. كما توفر سوقًا مجتمعيًا لبيع وتأجير وتبادل المنتجات بين السكان مع إدارة الطلبات والمدفوعات والتأمينات. تعتمد المنصة على تقنيات الذكاء الاصطناعي وتحليل البيانات لتحسين جودة الحياة، دعم اتخاذ القرار، وتعزيز المشاركة المجتمعية داخل الأحياء.
English
Mena & Feena is a smart community platform designed to strengthen social connections and cooperation among neighborhood residents through integrated digital services. The platform enables residents to report issues, participate in community events and initiatives, connect with neighbors, vote for a neighborhood mayor, and explore nearby landmarks. It also provides a community marketplace for buying, renting, and exchanging products, along with order management, payment processing, and security deposits. By leveraging Artificial Intelligence and data analytics, the platform aims to improve quality of life, support decision-making, and increase community engagement within neighborhoods.


⸻


📊 System Diagrams
Class Diagram
Add Class Diagram Image Here


⸻


Use Case Diagram
Add Use Case Diagram Image Here


⸻


🚀 Deployment
Deployment URL:
Add Deployment Link Here


⸻


📮 API Documentation
Postman Documentation:
Add Postman Documentation Link Here


⸻


🛠 Technologies Used
Java
Spring Boot
Spring Security
JWT Authentication
Spring Data JPA
MySQL
OpenAI API
WhatsApp API
Email Service
OpenStreetMap API
Overpass API
PDF Generation
Postman


⸻


📦 Modules Implemented
Models
User
Neighborhood
FamilyMember
Landmark
ElectionRound
MayorCandidate
MayorProfile
MayorVote


⸻


Security
JwtService
JwtAuthenticationFilter


⸻


🔐 Authentication
Auth Controller
Register
POST /api/v1/auth/register
Login
POST /api/v1/auth/login


⸻


🗳 Election Round Controller
Get All Election Rounds
GET /api/v1/election-rounds/get-all
Get Election Round Details
GET /api/v1/election-rounds/get/{roundId}/details
Create Election Round
POST /api/v1/election-rounds/add
Update Election Round
PUT /api/v1/election-rounds/update/{roundId}
Delete Election Round
DELETE /api/v1/election-rounds/delete/{roundId}


⸻


👨‍👩‍👧 Family Members Controller
CRUD Operations
GET    /api/v1/family-members/get-all
POST   /api/v1/family-members/add
PUT    /api/v1/family-members/update/{familyMemberId}
DELETE /api/v1/family-members/delete/{familyMemberId}


⸻


📍 Landmark Controller
CRUD Operations
GET    /api/v1/landmarks/get-all
POST   /api/v1/landmarks/add
PUT    /api/v1/landmarks/update/{landmarkId}
DELETE /api/v1/landmarks/delete/{landmarkId}
Sync Landmarks
POST /api/v1/landmarks/sync
Nearby Landmarks
GET /api/v1/landmarks/nearby
Landmark Dashboard
GET /api/v1/landmarks/dashboard


⸻


👑 Mayor Candidate Controller
Get All Candidates
GET /api/v1/mayor-candidates/get-all
Apply For Candidacy
POST /api/v1/mayor-candidates/apply/round/{roundId}
Get Candidates By Round
GET /api/v1/mayor-candidates/round/{roundId}
Election Dashboard
GET /api/v1/mayor-candidates/round/{roundId}/dashboard
Candidate Profile
GET /api/v1/mayor-candidates/profile/{candidateId}
Update Candidate
PUT /api/v1/mayor-candidates/update/{candidateId}
Delete Candidate
DELETE /api/v1/mayor-candidates/delete/{candidateId}


⸻


🏛 Mayor Profile Controller
CRUD Operations
GET    /api/v1/mayor-profile/get-all
POST   /api/v1/mayor-profile/add
PUT    /api/v1/mayor-profile/update/{id}
DELETE /api/v1/mayor-profile/delete/{id}
Analytics
GET /api/v1/mayor-profile/analytics
Reports
GET /api/v1/mayor-profile/reports
Weekly Report
GET /api/v1/mayor-profile/weekly
Performance Report
GET /api/v1/mayor-profile/performance
Satisfaction Report
GET /api/v1/mayor-profile/satisfaction


⸻


🗳 Mayor Vote Controller
Get All Votes
GET /api/v1/mayor-votes/get-all
Vote For Candidate
POST /api/v1/mayor-votes/vote/candidate/{candidateId}/round/{roundId}
Delete Vote
DELETE /api/v1/mayor-votes/delete/{voteId}


⸻


🏘 Neighborhood Controller
Get All Neighborhoods
GET /api/v1/neighborhoods/get-all
CRUD Operations
POST   /api/v1/neighborhoods/add
PUT    /api/v1/neighborhoods/update/{neighborhoodId}
DELETE /api/v1/neighborhoods/delete/{neighborhoodId}
Neighborhood Dashboard
GET /api/v1/neighborhoods/dashboard


⸻


👤 User Controller
Get All Users
GET /api/v1/users/get-all
CRUD Operations
POST   /api/v1/users/add
PUT    /api/v1/users/update/{userId}
DELETE /api/v1/users/delete/{userId}
General Pages
GET  /api/v1/users/welcome
GET  /api/v1/users/about
POST /api/v1/users/contact
Neighborhood Residents
GET /api/v1/users/neighborhood-residents
Activity Log
GET /api/v1/users/activity-log
User Profiles
GET /api/v1/users/profile/full
GET /api/v1/users/profile/basic
GET /api/v1/users/profile/family
GET /api/v1/users/profile/votes
GET /api/v1/users/profile/events
GET /api/v1/users/profile/reviews
GET /api/v1/users/profile/issues
Marketplace Profile
GET /api/v1/users/marketplace/summary
GET /api/v1/users/marketplace/my-orders
GET /api/v1/users/marketplace/product-orders


⸻


👨‍💻 Team Members
Reenad Almadhi
Walaa Alrashidi
Abdullah Alrasheed


⸻


🎓 Project
Tuwaiq Academy – Java Web Development Bootcamp Final Project
