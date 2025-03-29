LOAD CSV WITH HEADERS FROM 'file:///doctors.csv' AS row
CREATE (d:Doctor {doctorID: row.ID})
SET
d.name = row.Name,
d.phone = row.Phone,
d.speciality = row.Speciality

LOAD CSV WITH HEADERS FROM 'file:///patients.csv' AS row
CREATE (p:Patient {patientID: row.ID})
SET
p.name = row.Name,
p.phone = row.Phone,
p.gender = row.Gender,
p.dob = row.DateOfBirth,
p.address = row.Address

LOAD CSV WITH HEADERS FROM 'file:///departments.csv' AS row
CREATE (d:Department {deptID: row.id})
SET
d.name = row.name,
d.location = row.location

MATCH (n) DETACH DELETE n;
//CREATE CONSTRAINT uq_doctor
//FOR (d:Doctor) REQUIRE d.doctorID IS UNIQUE
//
//CREATE CONSTRAINT uq_patient
//FOR (p:Patient) REQUIRE p.patientID IS UNIQUE
//
//CREATE CONSTRAINT uq_dept
//FOR (d:Department) REQUIRE d.deptID IS UNIQUE

LOAD CSV WITH HEADERS FROM 'file:///doctors.csv' AS row
WITH row WHERE row.ID IS NOT NULL AND row.DepartmentID IS NOT NULL
MERGE (d:Doctor {doctorID: row.ID})
MERGE (de:Department {deptID: row.DepartmentID})
MERGE (d)-[:BELONG_TO]->(de);

LOAD CSV WITH HEADERS FROM 'file:///treatments.csv' AS row
WITH row WHERE row.DoctorID IS NOT NULL AND row.PatientID IS NOT NULL
MERGE (p:Patient {patientID: row.PatientID})
MERGE (d:Doctor {doctorID: row.DoctorID})
MERGE (p)-[r:BE_TREATED {startDate: date(row.StartDate)}]->(d)
SET r.endDate = date(row.EndDate),
r.diagnosis = row.Diagnosis;

//1. Thêm mới một bác sỹ.
CREATE (d:Doctor{doctorID: 'DR-STRANGE', name: 'Dr.Strange', phone: '0000-2222-22334', speciality: 'Magic'});

//2. Thống kê số bác sỹ theo từng chuyên khoa (speciality) của một khoa (department) nào đó khi biết tên khoa
MATCH (d:Doctor)-[:BELONG_TO]->(de:Department)
WHERE de.name = 'Internal Medicine'
WITH count(d) AS soluong, d.speciality AS spec
RETURN spec, soluong

//3. Dùng full-text search, tìm kiếm các bác sỹ theo chuyên khoa
//CREATE FULLTEXT INDEX spec_index
//FOR (d:Doctor) ON EACH [d.speciality];

//MATCH (d:Doctor)
//WHERE d.speciality CONTAINS 'Internal'
//RETURN d.name;

CALL db.index.fulltext.queryNodes('spec_index', 'Internal') YIELD node, score
RETURN node, score

//4. Cập nhật lại chẩn đoán (diagnosis) của một lượt điều trị khi biết mã số bác sỹ và mã
//số bệnh nhân. Lưu ý, chỉ được phép cập nhật khi lượt điều trị này vẫn còn đang điều
//trị (tức ngày kết thúc điều trị là null)
MATCH (p:Patient{patientID: 'ID'})-[r:BE_TREATED]->(d:Doctor{doctorID: 'ID'})
SET r.diagnosis = 'not trivial'

