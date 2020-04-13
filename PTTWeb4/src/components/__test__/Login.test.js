// import React from 'react';
// import { render } from '@testing-library/react';
// import Login from '../Login';
// import ReactDOM from 'react-dom';

// import renderer from 'react-test-renderer'; 

// test('renders without crashing', () => {
//     const div = document.createElement("div");
//     ReactDOM.render(<Login></Login>, div)
// });

// it("matches snapshot", () =>{
//     const tree = renderer.create(<Login></Login>).toJSON();
//     expect(tree).toMatchSnapshot();
// })

import React from 'react';
import ReactDOM from 'react-dom';
import Login from '../Login';
import renderer from 'react-test-renderer';
import {shallow} from 'enzyme';
import toJSON from 'enzyme-to-json';
import { Provider } from "react-redux";
import configureMockStore from "redux-mock-store";

const mockStore = configureMockStore();
const store = mockStore({});


test("Login renders correctly",()=>{
    const wrapper = shallow(<Provider store={store}> <Login /> </Provider>)

    expect(toJSON(wrapper)).toMatchSnapshot();
})

import {loginActionCreators} from '../../models/actions/loginPageActions';
// import * as types from '../../constants/ActionTypes'
import { actionTypes } from '../../models/actions/loginPageActions';

describe('Login actions', () => {
    it('should create a user', () => {
      const id = '123456'
      const success = true 
  
      const expectedAction = {
          userId: id,
          logingSuccess: success,
          type: actionTypes.SUBMIT_LOGIN
      }
      expect(loginActionCreators.submitLogin(id, success)).toEqual(expectedAction)
    })
  
})
