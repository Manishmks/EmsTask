# EMS
A Spring Boot project for Employee Management System
## Table of Contents
- [Database](#database)
    - [Designation](#designation)
    - [Employee](#employee)
- [Example Date](#example-data)
    - [Designation](#designation)
    - [Employee](#employee)
- [API](#api)
    
## Database
#### Designation
- id: Integer (Primary Key)
- level: Float
- title: String
#### Employee
- id: Integer (Primary Key)
- name: String
- manager: Integer
- designation: Designation (Reference)
### Example Data
Designation
| id  | level | title     |
| --- | ----- | --------- |
| 1   | 1     | Director  |
| 2   | 2     | Manager   |
| 3   | 3     | Lead      |
| 4   | 4     | Developer |
| 5   | 4     | DevOps    | 
| 6   | 4     | QA        |
| 7   | 5     | Intern    |
Employee
| id  | name            | manager | designation   |
| --- | --------------- | ------- | ------------- |
| 1   | Thor            | null    | 1 (Director)  |
| 2   | Iron Man        | 1       | 2 (Manager)   |
| 3   | Hulk            | 1       | 3 (Lead)      |
| 4   | Captain America | 1       | 2 (Manager)   |
| 5   | War Machine     | 2       | 6 (QA)        |
| 6   | Vision          | 2       | 5 (DevOps)    |
| 7   | Falcon          | 4       | 4 (Developer) |
| 8   | Ant Man         | 4       | 3 (Lead)      |
| 9   | Spider Man      | 2       | 7 (Intern)    |
| 10  | Black kWidow    | 3       | 4 (Developer) |
## API
#### Error Codes
- 200: OK
- 404: Resource Not Found
- 405: Method Not Allowed
- 406: Not Acceptable
#### GET /employees
Returns list of all employees
Request
```
GET /rest/employees
```
Response
```json
[
  {
    "id": 1,
    "jobTitle": "Director",
    "managerId": null,
    "name": "THOR"
  },
  {
    "id": 4,
    "jobTitle": "Manager",
    "managerId": 1,
    "name": "CAPTAINAMERICA"
  },
  {
    "id": 2,
    "jobTitle": "Manager",
    "managerId": 1,
    "name": "IRONMAN"
  },
  {
    "id": 10,
    "jobTitle": "Lead",
    "managerId": 4,
    "name": "ANTMAN"
  },
  {
    "id": 3,
    "jobTitle": "Lead",
    "managerId": 1,
    "name": "HULK"
  },
  {
    "id": 8,
    "jobTitle": "Developer",
    "managerId": 3,
    "name": "BLACKWIDOW"
  },
  {
    "id": 9,
    "jobTitle": "Developer",
    "managerId": 4,
    "name": "FALCON"
  },
  {
    "id": 6,
    "jobTitle": "DevOps",
    "managerId": 2,
    "name": "VISION"
  },
  {
    "id": 5,
    "jobTitle": "QA",
    "managerId": 2,
    "name": "WARMACHINE"
  },
  {
    "id": 7,
    "jobTitle": "Intern",
    "managerId": 2,
    "name": "SPIDERMAN"
  }
]
```
### POST /employees
Add a new employee
Body
```json
{
  "name": "String Required - Employee Name",
  "jobTitle": "String Required - Employee Designation",
  "managerId": "Integer Optional - Manager Employee ID, Required if current employee is not Director"
}
```
Request
```
POST /rest/employee
body: {
    "name": "Dr Strange",
    "jobTitle": "Manager",
    "managerId": 1
}
```
Response
```json
{
    "id": 11,
    "name": "Dr Strange",
    "jobTitle": "Manager",
    "manager": {
        "id": 1,
        "name": "Thor",
        "jobTitle": "Director"
    },
    "colleagues": [
        {
            "id": 4,
            "name": "Captain America",
            "jobTitle": "Manager"
        },
        {
            "id": 2,
            "name": "Iron Man",
            "jobTitle": "Manager"
        },
        {
            "id": 3,
            "name": "Hulk",
            "jobTitle": "Lead"
        }
    ]
}
```
### GET employee/{id}
Returns info of specific employee according to ID
Request
```
GET /rest/employee/2
```
Response
```json
{
  "employee": {
    "id": 2,
    "jobTitle": "Manager",
    "managerId": 1,
    "name": "IRONMAN"
  },
  "manager": {
    "id": 1,
    "jobTitle": "Director",
    "managerId": null,
    "name": "THOR"
  },
  "colleagues": [
    {
      "id": 4,
      "jobTitle": "Manager",
      "managerId": 1,
      "name": "CAPTAINAMERICA"
    },
    {
      "id": 3,
      "jobTitle": "Lead",
      "managerId": 1,
      "name": "HULK"
    }
  ],
  "subordinates": [
    {
      "id": 6,
      "jobTitle": "DevOps",
      "managerId": 2,
      "name": "VISION"
    },
    {
      "id": 5,
      "jobTitle": "QA",
      "managerId": 2,
      "name": "WARMACHINE"
    },
    {
      "id": 7,
      "jobTitle": "Intern",
      "managerId": 2,
      "name": "SPIDERMAN"
    }
  ]
}
```
#### PUT /employee/${id}
Update or replace employee by ID
Body
```json
{
  "name": "String Required - Employee Name",
  "jobTitle": "String Required - Employee Designation",
  "managerId": "Integer Optional - Manager Employee ID, Required if current employee is not Director",
  "replace": "Boolean Optional - Replace old employee with current employee"
}
```
Request
```
PUT /rest/employee/3
body: {
    "name": "Black Panther",
    "jobTitle": "Lead",
    "managerId": 1,
    "replace": true
}
```
Response
```json
{
    "id": 12,
    "name": "Black Panther",
    "jobTitle": "Lead",
    "manager": {
        "id": 1,
        "name": "Thor",
        "jobTitle": "Director"
    },
    "colleagues": [
        {
            "id": 4,
            "name": "Captain America",
            "jobTitle": "Manager"
        },
        {
            "id": 2,
            "name": "Iron Man",
            "jobTitle": "Manager"
        }
    ],
    "subordinates": [
      {
        "id":10,
        "name":"Black Widow",
        "jobTitle":"Developer"
      }
    ]
}
```
### DELETE /employee/${id}
Delete employee by ID
Request
```
DELETE /rest/employee/10
```
Response
```
OK
```
