import React, { Component } from 'react';
import { BrowserRouter, Route, Switch } from 'react-router-dom';
// import teal from '@material-ui/core/colors/teal';

// import Header from './components/Header.js';
// import Footer from './components/Footer.js';
// import Login from './components/Login.js';
// import AdminDashboard from './components/AdminDashboard';
// import UserDashboard from './components/UserDashboard';

import PublicRoutes from './components/PublicRoutes';
import ProtectedRoutes from './components/ProtectedRoutes';

import './app.scss';

// const primary = teal;

class App extends Component {

  render() {
    return (
      <div className="wrapper" >
        <BrowserRouter>
          <Switch>
            <Route path='/ptt' component={ProtectedRoutes} />
            <Route path='/' component={PublicRoutes} />
          </Switch>
        </BrowserRouter>
      </div>
    );
  }
}

export default App;;
