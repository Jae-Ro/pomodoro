![alt text](/case_study/Timer%20Start.png)

This project was bootstrapped with [Create React App](https://github.com/facebook/create-react-app).

## How to Run Pomodoro App - Backend
In the project directory 'backend', do the following:

Firstly, install [python3](https://www.python.org/downloads/)
Then install [pip3](https://pip.pypa.io/en/stable/) for python 3

Then use the following commands to setup the server:

1. `cd backend/src`
2. `pip3 install -r requirements.txt`
3. `cd backend`
4. `bash cleandb.sh`

Use the following command to run the server:
`python manage.py runserver`

This will run the server on `http://127.0.0.1:8000/`

## How to Run Pomodoro App - Frontend

In the project directory 'frontend', you can run:

### `npm install` 
This will download all necessary dependencies. Once that is finished, 
please run:

### Create a `.env.staging` file in the root of the frontend folder
Within this file, add the following:
REACT_APP_API_URL = "http://127.0.0.1:8000/"

### `npm run start:dev`

Runs the app in the development mode with the backend running locally.<br />
Open [http://localhost:3000](http://localhost:3000) to view it in the browser.

In the event that you get an error related to node-sass: try running "npm rebuild node-sass"

The page will reload if you make edits.<br />
You will also see any lint errors in the console.

Please use the following paths to access different things in the app:
 - http://localhost:3000 - Login page (to login as an admin type "Admin" into the email field)
 - http://localhost:3000/app/admin - Admin dashboard (for adding, editing, deleting Users)
 - http://localhost:3000/app/user - User dashboard (for adding, editing, deleting Projects)
 - 



------------------------------------------------
## Optional Commands

### `npm test`

Launches the test runner in the interactive watch mode. 
It should run all tests, and the expected output should resemble the following for all test cases concerning the first set of user stories:<br />

```
Test Suites: 7 passed, 7 total
Tests:       20 passed, 20 total
Snapshots:   7 passed, 7 total
Time:        13.644s
Ran all test suites.
No tests found related to files changed since last commit.
Press `a` to run all tests, or run Jest with `--watchAll`.
```

If you by any chance encounter the following:
```
No tests found related to files changed since last commit.
Press `a` to run all tests, or run Jest with `--watchAll`.

Watch Usage
 › Press a to run all tests.
 › Press f to run only failed tests.
 › Press q to quit watch mode.
 › Press p to filter by a filename regex pattern. 
 › Press t to filter by a test name regex pattern.
 › Press Enter to trigger a test run.
```
simply type the letter 'a' on your keyboard, and it should run all the tests in the __test__ directory.<br /> 
Tests can be found under <dir>/frontend/src/components/__test__/<br />

See the section about [running tests](https://facebook.github.io/create-react-app/docs/running-tests) for more information.

### `npm run build`

Builds the app for production to the `build` folder.<br />
It correctly bundles React in production mode and optimizes the build for the best performance.

The build is minified and the filenames include the hashes.<br />
Your app is ready to be deployed!

See the section about [deployment](https://facebook.github.io/create-react-app/docs/deployment) for more information.

### `npm run eject`

**Note: this is a one-way operation. Once you `eject`, you can’t go back!**

If you aren’t satisfied with the build tool and configuration choices, you can `eject` at any time. This command will remove the single build dependency from your project.

Instead, it will copy all the configuration files and the transitive dependencies (webpack, Babel, ESLint, etc) right into your project so you have full control over them. All of the commands except `eject` will still work, but they will point to the copied scripts so you can tweak them. At this point you’re on your own.

You don’t have to ever use `eject`. The curated feature set is suitable for small and middle deployments, and you shouldn’t feel obligated to use this feature. However we understand that this tool wouldn’t be useful if you couldn’t customize it when you are ready for it.

## Learn More

You can learn more in the [Create React App documentation](https://facebook.github.io/create-react-app/docs/getting-started).

To learn React, check out the [React documentation](https://reactjs.org/).

### Code Splitting

This section has moved here: https://facebook.github.io/create-react-app/docs/code-splitting

### Analyzing the Bundle Size

This section has moved here: https://facebook.github.io/create-react-app/docs/analyzing-the-bundle-size

### Making a Progressive Web App

This section has moved here: https://facebook.github.io/create-react-app/docs/making-a-progressive-web-app

### Advanced Configuration

This section has moved here: https://facebook.github.io/create-react-app/docs/advanced-configuration

### Deployment

This section has moved here: https://facebook.github.io/create-react-app/docs/deployment

### `npm run build` fails to minify

This section has moved here: https://facebook.github.io/create-react-app/docs/troubleshooting#npm-run-build-fails-to-minify
