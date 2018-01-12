## Predictive Analytics as a Service

A Microservice approach to offer Predictive Analytics as a REST API encapsulating various machine learning modelling 
algorithms. The aim is to build a predictive model simply by supplying the necessary configurations in a simple JSON file.

The training and prediction endpoints are exposed via a simple REST API.

### Datasets
Training and building the model needs a good dataset. Currently, this app only supports the dataset sourced from a CSV file.
In the future versions, other datasoures such as RDBMS, MONGODB will be supported.

### Sample Model JSON
This is how a simple model definition for an income-classification data looks like:
```
{
  "id": "income-classification",
  "description": "Income classification",
  "trainingIterations": 5,
  "datasourceType": "CSV",
  "datasourceValue": "income.csv",
  "inputColumns": [
    {
      "name": "age",
      "kind": "continuous",
      "missingValue": "Mean"
    },
    {
      "name": "workclass",
      "kind": "ordinal",
      "enumerations": [
        "Private",
        "Self-emp-not-inc",
        "Self-emp-inc",
        "Federal-gov",
        "Local-gov",
        "State-gov",
        "Without-pay",
        "Never-worked",
        "?"
      ],
      "missingValue": "QuestionMarkString"
    },
    {
      "name": "fnlwgt",
      "kind": "continuous",
      "missingValue": "Mean"
    },
    {
      "name": "education",
      "kind": "ordinal",
      "enumerations": [
        "Bachelors",
        "Some-college",
        "11th",
        "HS-grad",
        "Prof-school",
        "Assoc-acdm",
        "Assoc-voc",
        "9th",
        "7th-8th",
        "12th",
        "Masters",
        "1st-4th",
        "10th",
        "Doctorate",
        "5th-6th",
        "Preschool",
        "?"
      ],
      "missingValue": "QuestionMarkString"
    },
    {
      "name": "education-num",
      "kind": "continuous",
      "missingValue": "Mean"
    },
    {
      "name": "marital-status",
      "kind": "ordinal",
      "enumerations": [
        "Married-civ-spouse",
        "Divorced",
        "Never-married",
        "Separated",
        "Widowed",
        "Married-spouse-absent",
        "Married-AF-spouse",
        "?"
      ],
      "missingValue": "QuestionMarkString"
    },
    {
      "name": "occupation",
      "kind": "ordinal",
      "enumerations": [
        "Tech-support",
        "Craft-repair",
        "Other-service",
        "Sales",
        "Exec-managerial",
        "Prof-specialty",
        "Handlers-cleaners",
        "Machine-op-inspct",
        "Adm-clerical",
        "Farming-fishing",
        "Transport-moving",
        "Priv-house-serv",
        "Protective-serv",
        "Armed-Forces",
        "?"
      ],
      "missingValue": "QuestionMarkString"
    },
    {
      "name": "relationship",
      "kind": "ordinal",
      "enumerations": [
        "Wife",
        "Own-child",
        "Husband",
        "Not-in-family",
        "Other-relative",
        "Unmarried",
        "?"
      ],
      "missingValue": "QuestionMarkString"
    },
    {
      "name": "race",
      "kind": "ordinal",
      "enumerations": [
        "White",
        "Asian-Pac-Islander",
        "Amer-Indian-Eskimo",
        "Other",
        "Black",
        "?"
      ],
      "missingValue": "QuestionMarkString"
    },
    {
      "name": "sex",
      "kind": "ordinal",
      "enumerations": [
        "Female",
        "Male",
        "?"
      ],
      "missingValue": "QuestionMarkString"
    },
    {
      "name": "capital-gain",
      "kind": "continuous",
      "missingValue": "Mean"
    },
    {
      "name": "capital-loss",
      "kind": "continuous",
      "missingValue": "Mean"
    },
    {
      "name": "hours-per-week",
      "kind": "continuous",
      "missingValue": "Mean"
    },
    {
      "name": "native-country",
      "kind": "ordinal",
      "enumerations": [
        "United-States",
        "Cambodia",
        "England",
        "Puerto-Rico",
        "Canada",
        "Germany",
        "Outlying-US(Guam-USVI-etc)",
        "India",
        "Japan",
        "Greece",
        "South",
        "China",
        "Cuba",
        "Iran",
        "Honduras",
        "Philippines",
        "Italy",
        "Poland",
        "Jamaica",
        "Vietnam",
        "Mexico",
        "Portugal",
        "Ireland",
        "France",
        "Dominican-Republic",
        "Laos",
        "Ecuador",
        "Taiwan",
        "Haiti",
        "Columbia",
        "Hungary",
        "Guatemala",
        "Nicaragua",
        "Scotland",
        "Thailand",
        "Yugoslavia",
        "El-Salvador",
        "Trinadad&Tobago",
        "Peru",
        "Hong",
        "Holand-Netherlands",
        "?"
      ],
      "missingValue": "QuestionMarkString"
    }
  ],
  "unknownValueRepresentedAs": "?",
  "predictedColumn": {
    "name": "income",
    "kind": "nominal"
  },
  "problemType": "classification",
  "modelType": "feedforward"
}
```

A sample income.csv file looks like this (just a few lines):

```
39, State-gov, 77516, Bachelors, 13, Never-married, Adm-clerical, Not-in-family, White, Male, 2174, 0, 40, United-States, <=50K
50, Self-emp-not-inc, 83311, Bachelors, 13, Married-civ-spouse, Exec-managerial, Husband, White, Male, 0, 0, 13, United-States, <=50K
38, Private, 215646, HS-grad, 9, Divorced, Handlers-cleaners, Not-in-family, White, Male, 0, 0, 40, United-States, <=50K
53, Private, 234721, 11th, 7, Married-civ-spouse, Handlers-cleaners, Husband, Black, Male, 0, 0, 40, United-States, <=50K
28, Private, 338409, Bachelors, 13, Married-civ-spouse, Prof-specialty, Wife, Black, Female, 0, 0, 40, Cuba, <=50K
37, Private, 284582, Masters, 14, Married-civ-spouse, Exec-managerial, Wife, White, Female, 0, 0, 40, United-States, <=50K
49, Private, 160187, 9th, 5, Married-spouse-absent, Other-service, Not-in-family, Black, Female, 0, 0, 16, Jamaica, <=50K
```

The fields are defined in the JSON above.

## Modelling Algorithms supported
**feedforward, bayesian, rbfnetwork, svm, som, pnn, neat, epl**
modelType in the configuration JSON reflects this.

Whenever the model is changed, you must retrain and rebuild the model.


## To build the app
* Git clone
* Do a mvn clean install
* Run the executable jar file in the target directory using the java -jar ... command

Note that this uses an inmemory database for storing the model configs for testing purposes. 
You can switch to a real RDBMS by changing the values in the src/main/resources/bootstrap.properties (standard spring boot app)

## Rest API
* Once the app is up and running, http://localhost:52073/swagger-ui.html# lists you all the APIs.
* To kick off the training, use the Training API.
* To Predict, use the predictive-model-api - Also this will be the API to CRUD the configurations.

## Simple View UI
* * Once the app is up and running, http://localhost:52073/ lists you all the available models in a simple UI.

![Models UI](ui.png?raw=true "Rule Audit ")

## License
This project is licensed under Apache License 2.0




